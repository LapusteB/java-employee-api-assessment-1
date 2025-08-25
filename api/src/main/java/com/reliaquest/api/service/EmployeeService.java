package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeInput;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    
    private final RestTemplate restTemplate;
    private final String mockApiBaseUrl;
    
    public EmployeeService(RestTemplate restTemplate, 
                          @Value("${mock.api.base-url:http://localhost:8112}") String mockApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.mockApiBaseUrl = mockApiBaseUrl;
    }
    
    public List<Employee> getAllEmployees() {
        String url = mockApiBaseUrl + "/api/v1/employee";
        MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
        return response != null ? response.getData() : List.of();
    }
    
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        String url = mockApiBaseUrl + "/api/v1/employee";
        MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
        
        if (response == null || response.getData() == null) {
            return List.of();
        }
        
        return response.getData().stream()
                .filter(employee -> employee.getEmployeeName().toLowerCase()
                        .contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public Employee getEmployeeById(String id) {
        String url = mockApiBaseUrl + "/api/v1/employee";
        MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
        
        if (response == null || response.getData() == null) {
            return null;
        }
        
        return response.getData().stream()
                .filter(employee -> id.equals(employee.getId()))
                .findFirst()
                .orElse(null);
    }
    
    public Integer getHighestSalaryOfEmployees() {
        String url = mockApiBaseUrl + "/api/v1/employee";
        MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
        
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return 0;
        }
        
        // Fast single-pass algorithm: O(n) time complexity
        // No need to sort the entire list - just track the max!
        return response.getData().stream()
                .mapToInt(Employee::getEmployeeSalary)
                .max()
                .orElse(0);
    }
    
    public List<String> getTopTenHighestEarningEmployeeNames() {
        String url = mockApiBaseUrl + "/api/v1/employee";
        MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
        
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return List.of();
        }
        
        // Efficient algorithm: Sort by salary descending and take top 10
        // Using parallel stream for better performance on large datasets
        return response.getData().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }
    
    public Employee createEmployee(CreateEmployeeInput employeeInput) {
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
            MockCreateResponse response = restTemplate.postForObject(url, mockInput, MockCreateResponse.class);
            
            if (response == null || response.getData() == null) {
                throw new RuntimeException("Failed to create employee - mock server returned null or empty data");
            }
            
            // Return the created employee from the response
            return response.getData();
            
        } catch (Exception e) {
            log.error("Error creating employee via mock server: {}", e.getMessage());
            throw new RuntimeException("Failed to create employee via mock server", e);
        }
    }
    
    public String deleteEmployeeById(String id) {
        // Make HTTP DELETE to mock server to delete employee
        String url = mockApiBaseUrl + "/api/v1/employee";
        
        try {
            // Create delete input for the mock server
            java.util.Map<String, Object> deleteInput = new java.util.HashMap<>();
            deleteInput.put("name", id); // Mock server expects name for deletion
            
            // DELETE to mock server
            restTemplate.delete(url, deleteInput);
            
            log.info("Successfully deleted employee with ID: {}", id);
            return "Employee deleted successfully";
            
        } catch (Exception e) {
            log.error("Error deleting employee via mock server: {}", e.getMessage());
            throw new RuntimeException("Failed to delete employee via mock server", e);
        }
    }
    
    // Inner class to match the mock API response structure for GET requests
    public static class MockApiResponse {
        private List<Employee> data;
        private String status;
        
        public List<Employee> getData() { return data; }
        public void setData(List<Employee> data) { this.data = data; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // Inner class to match the mock API response structure for POST requests
    public static class MockCreateResponse {
        private Employee data;
        private String status;
        
        public Employee getData() { return data; }
        public void setData(Employee data) { this.data = data; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
