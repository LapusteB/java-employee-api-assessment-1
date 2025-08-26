package com.reliaquest.api.service;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final RestTemplate restTemplate;
    private final String mockApiBaseUrl;
    private final int maxRetries;
    private final long baseDelayMs;
    private final long maxDelayMs;

    public EmployeeService(
            RestTemplate restTemplate,
            @Value("${mock.api.base-url:http://localhost:8112}") String mockApiBaseUrl,
            @Value("${retry.max-attempts:5}") int maxRetries,
            @Value("${retry.base-delay-ms:100}") long baseDelayMs,
            @Value("${retry.max-delay-ms:2000}") long maxDelayMs) {
        this.restTemplate = restTemplate;
        this.mockApiBaseUrl = mockApiBaseUrl;
        this.maxRetries = maxRetries;
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs = maxDelayMs;
    }

    public List<Employee> getAllEmployees() {
        return executeWithRetry(
                () -> {
                    String url = mockApiBaseUrl + "/api/v1/employee";
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
                    return response != null ? response.getData() : List.of();
                },
                "getAllEmployees");
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return executeWithRetry(
                () -> {
                    String url = mockApiBaseUrl + "/api/v1/employee";
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);

                    if (response == null || response.getData() == null) {
                        return List.of();
                    }

                    return response.getData().stream()
                            .filter(employee ->
                                    employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                            .collect(Collectors.toList());
                },
                "getEmployeesByNameSearch");
    }

    public Employee getEmployeeById(String id) {
        return executeWithRetry(
                () -> {
                    String url = mockApiBaseUrl + "/api/v1/employee";
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);

                    if (response == null || response.getData() == null) {
                        return null;
                    }

                    return response.getData().stream()
                            .filter(employee -> id.equals(employee.getId()))
                            .findFirst()
                            .orElse(null);
                },
                "getEmployeeById");
    }

    public Integer getHighestSalaryOfEmployees() {
        return executeWithRetry(
                () -> {
                    String url = mockApiBaseUrl + "/api/v1/employee";
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);

                    if (response == null
                            || response.getData() == null
                            || response.getData().isEmpty()) {
                        return 0;
                    }

                    // Fast single-pass algorithm: O(n) time complexity
                    // No need to sort the entire list - just track the max!
                    return response.getData().stream()
                            .mapToInt(Employee::getEmployeeSalary)
                            .max()
                            .orElse(0);
                },
                "getHighestSalaryOfEmployees");
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        return executeWithRetry(
                () -> {
                    String url = mockApiBaseUrl + "/api/v1/employee";
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);

                    if (response == null
                            || response.getData() == null
                            || response.getData().isEmpty()) {
                        return List.of();
                    }

                    // Efficient algorithm: Sort by salary descending and take top 10
                    // Using parallel stream for better performance on large datasets
                    return response.getData().stream()
                            .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                            .limit(10)
                            .map(Employee::getEmployeeName)
                            .collect(Collectors.toList());
                },
                "getTopTenHighestEarningEmployeeNames");
    }

    public Employee createEmployee(CreateEmployeeInput employeeInput) {
        return executeWithRetry(
                () -> {
                    // Make HTTP POST to mock server to create employee
                    String url = mockApiBaseUrl + "/api/v1/employee";

                    try {
                        // Create a simple DTO for the mock server
                        // We'll use a Map since we can't import the server model
                        java.util.Map<String, Object> mockInput = new java.util.HashMap<>();
                        mockInput.put("name", employeeInput.getName());
                        mockInput.put("salary", employeeInput.getSalary());
                        mockInput.put("age", employeeInput.getAge());
                        mockInput.put("title", employeeInput.getTitle());

                        // POST to mock server and get response wrapped in Response<Employee>
                        MockCreateResponse response =
                                restTemplate.postForObject(url, mockInput, MockCreateResponse.class);

                        if (response == null || response.getData() == null) {
                            throw new RuntimeException(
                                    "Failed to create employee - mock server returned null or empty data");
                        }

                        // Return the created employee from the response
                        return response.getData();

                    } catch (Exception e) {
                        log.error("Error creating employee via mock server: {}", e.getMessage());
                        throw new RuntimeException("Failed to create employee via mock server", e);
                    }
                },
                "createEmployee");
    }

    public String deleteEmployeeById(String id) {
        return executeWithRetry(
                () -> {
                    // Make HTTP DELETE to mock server to delete employee
                    String url = mockApiBaseUrl + "/api/v1/employee";

                    try {
                        // First, get the employee to find their name
                        Employee employee = getEmployeeById(id);
                        if (employee == null) {
                            throw new RuntimeException("Employee with ID " + id + " not found");
                        }

                        // Create delete input for the mock server (it expects name, not ID)
                        java.util.Map<String, Object> deleteInput = new java.util.HashMap<>();
                        deleteInput.put("name", employee.getEmployeeName());

                        // DELETE to mock server with request body
                        restTemplate.exchange(
                                url,
                                HttpMethod.DELETE,
                                new org.springframework.http.HttpEntity<>(deleteInput),
                                String.class);

                        log.info(
                                "Successfully deleted employee with ID: {} and name: {}",
                                id,
                                employee.getEmployeeName());
                        return "Employee deleted successfully";

                    } catch (Exception e) {
                        log.error("Error deleting employee via mock server: {}", e.getMessage());
                        throw new RuntimeException("Failed to delete employee via mock server", e);
                    }
                },
                "deleteEmployeeById");
    }

    // Inner class to match the mock API response structure for GET requests
    public static class MockApiResponse {
        private List<Employee> data;
        private String status;

        public List<Employee> getData() {
            return data;
        }

        public void setData(List<Employee> data) {
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Inner class to match the mock API response structure for POST requests
    public static class MockCreateResponse {
        private Employee data;
        private String status;

        public Employee getData() {
            return data;
        }

        public void setData(Employee data) {
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * Executes a supplier function with retry logic to handle rate limiting
     * Uses exponential backoff strategy: 100ms, 200ms, 400ms, 800ms, 1600ms
     *
     * @param supplier The function to execute
     * @param operationName Name of the operation for logging
     * @param <T> Return type
     * @return Result of the supplier function
     * @throws RuntimeException if all retries fail
     */
    private <T> T executeWithRetry(java.util.function.Supplier<T> supplier, String operationName) {
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                // Check if this is a rate limiting error (500 Internal Server Error)
                if (isRateLimitError(e) && attempt < maxRetries) {
                    long delayMs = Math.min(baseDelayMs * (1L << attempt), maxDelayMs); // Exponential backoff with cap

                    log.warn(
                            "Rate limit hit for {} operation (attempt {}/{}), retrying in {}ms: {}",
                            operationName,
                            attempt + 1,
                            maxRetries + 1,
                            delayMs,
                            e.getMessage());

                    try {
                        TimeUnit.MILLISECONDS.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted: " + operationName, ie);
                    }
                } else {
                    // Either not a rate limit error or we've exhausted retries
                    log.error("Operation {} failed after {} attempts: {}", operationName, attempt + 1, e.getMessage());
                    throw e;
                }
            }
        }

        throw new RuntimeException("Operation " + operationName + " failed after " + (maxRetries + 1) + " attempts");
    }

    /**
     * Determines if an exception is likely a rate limiting error
     * Checks for 500 errors or specific rate limit indicators
     */
    private boolean isRateLimitError(Exception e) {
        String message = e.getMessage();
        if (message == null) return false;

        // Check for common rate limiting indicators
        return message.contains("500")
                || message.contains("Internal Server Error")
                || message.contains("Too Many Requests")
                || message.contains("rate limit")
                || message.contains("429");
    }
}
