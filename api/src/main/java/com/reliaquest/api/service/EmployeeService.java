package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class EmployeeService {
    
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
    
    // Inner class to match the mock API response structure
    public static class MockApiResponse {
        private List<Employee> data;
        private String status;
        
        public List<Employee> getData() { return data; }
        public void setData(List<Employee> data) { this.data = data; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
