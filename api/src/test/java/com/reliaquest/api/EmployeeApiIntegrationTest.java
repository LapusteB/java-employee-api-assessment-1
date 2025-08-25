package com.reliaquest.api;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/employee";
    }
    
    /**
     * Helper method to retry operations that might fail due to rate limiting
     * Uses exponential backoff strategy
     */
    private <T> T retryOperation(java.util.function.Supplier<T> operation, String operationName, int maxRetries) {
        long baseDelayMs = 200; // Start with 200ms delay
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                if (attempt < maxRetries) {
                    long delayMs = baseDelayMs * (1L << attempt); // Exponential backoff
                    System.out.println("Operation " + operationName + " failed (attempt " + (attempt + 1) + "/" + (maxRetries + 1) + 
                                    "), retrying in " + delayMs + "ms. Error: " + e.getMessage());
                    
                    try {
                        TimeUnit.MILLISECONDS.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Test interrupted", ie);
                    }
                } else {
                    throw e; // Re-throw on final attempt
                }
            }
        }
        throw new RuntimeException("Operation " + operationName + " failed after " + (maxRetries + 1) + " attempts");
    }

    @Test
    void testDeleteEmployee_IntegrationTest() {
        // Step 1: Create an employee first with retry logic
        CreateEmployeeInput createInput = new CreateEmployeeInput();
        createInput.setName("Delete Test Employee");
        createInput.setSalary(60000);
        createInput.setAge(28);
        createInput.setTitle("Test Developer");

        ResponseEntity<Employee> createResponse = retryOperation(
            () -> restTemplate.postForEntity(getBaseUrl(), createInput, Employee.class),
            "Create Employee",
            3
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String employeeId = createResponse.getBody().getId();
        
        System.out.println("Created employee with ID: " + employeeId);

        // Step 2: Verify employee exists with retry logic
        ResponseEntity<Employee> getResponse = retryOperation(
            () -> restTemplate.getForEntity(getBaseUrl() + "/" + employeeId, Employee.class),
            "Get Employee",
            3
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // Step 3: Delete the employee with retry logic
        ResponseEntity<String> deleteResponse = retryOperation(
            () -> restTemplate.exchange(
                getBaseUrl() + "/" + employeeId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
            ),
            "Delete Employee",
            3
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals("Employee deleted successfully", deleteResponse.getBody());
        
        System.out.println("Successfully deleted employee");

        // Step 4: Verify employee is gone with retry logic
        ResponseEntity<Employee> getAfterDeleteResponse = retryOperation(
            () -> restTemplate.getForEntity(getBaseUrl() + "/" + employeeId, Employee.class),
            "Get Employee After Delete",
            3
        );
        assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.getStatusCode());
        
        System.out.println("Verified employee no longer exists");
    }
}
