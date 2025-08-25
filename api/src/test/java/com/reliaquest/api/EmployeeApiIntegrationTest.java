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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/employee";
    }

    @Test
    void testDeleteEmployee_IntegrationTest() {
        // Step 1: Create an employee first
        CreateEmployeeInput createInput = new CreateEmployeeInput();
        createInput.setName("Delete Test Employee");
        createInput.setSalary(60000);
        createInput.setAge(28);
        createInput.setTitle("Test Developer");

        ResponseEntity<Employee> createResponse = restTemplate.postForEntity(
            getBaseUrl(), 
            createInput, 
            Employee.class
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String employeeId = createResponse.getBody().getId();
        
        System.out.println("✅ Created employee with ID: " + employeeId);

        // Step 2: Verify employee exists
        ResponseEntity<Employee> getResponse = restTemplate.getForEntity(
            getBaseUrl() + "/" + employeeId, 
            Employee.class
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // Step 3: Delete the employee
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
            getBaseUrl() + "/" + employeeId,
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            String.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals("Employee deleted successfully", deleteResponse.getBody());
        
        System.out.println("✅ Successfully deleted employee");

        // Step 4: Verify employee is gone
        ResponseEntity<Employee> getAfterDeleteResponse = restTemplate.getForEntity(
            getBaseUrl() + "/" + employeeId, 
            Employee.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.getStatusCode());
        
        System.out.println("✅ Verified employee no longer exists");
    }
}
