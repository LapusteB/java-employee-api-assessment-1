package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// I am used to using Mockito to create mock objects for testing

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    
    @Mock
    private EmployeeService employeeService;
    
    @InjectMocks
    private EmployeeController employeeController;
    
    private List<Employee> mockEmployees;
    
    @BeforeEach
    void setUp() {
        // Create mock employee data
        Employee employee1 = new Employee();
        employee1.setId("1");
        employee1.setEmployeeName("John Doe");
        employee1.setEmployeeSalary(50000);
        employee1.setEmployeeAge(30);
        employee1.setEmployeeTitle("Developer");
        employee1.setEmployeeEmail("john@company.com");
        
        Employee employee2 = new Employee();
        employee2.setId("2");
        employee2.setEmployeeName("Jane Smith");
        employee2.setEmployeeSalary(60000);
        employee2.setEmployeeAge(28);
        employee2.setEmployeeTitle("Senior Developer");
        employee2.setEmployeeEmail("jane@company.com");
        
        mockEmployees = Arrays.asList(employee1, employee2);
    }

    @Test // Run the backend mock server at localhost:8112
    void getAllEmployees_IntegrationTest() {
        // This test will call the real mock server at localhost:8112
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8112/api/v1/employee";
        
        try {
            // Call the real mock API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());
            System.out.println("Real API Response: " + response.getBody());
            assertEquals(200, response.getStatusCodeValue());
        } catch (Exception e) {
            System.out.println("ERROR: Could not connect to mock server at " + url);
            System.out.println("Make sure to run: ./gradlew server:bootRun first");
            System.out.println("Exception: " + e.getMessage());
            throw e; // Re-throw to fail the test
        }
    }
    
    @Test
    void getAllEmployees_ShouldReturnEmployeeList_WhenServiceReturnsEmployees() {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);
        
        // When
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }
    
    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenServiceReturnsEmptyList() {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList());
        
        // When
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getAllEmployees();
    }
    
    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees_WhenServiceReturnsEmployees() {
        // Given
        String searchTerm = "John";
        List<Employee> matchingEmployees = Arrays.asList(mockEmployees.get(0)); // John Doe
        when(employeeService.getEmployeesByNameSearch(searchTerm)).thenReturn(matchingEmployees);
        
        // When
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchTerm);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getEmployeeName());
        verify(employeeService, times(1)).getEmployeesByNameSearch(searchTerm);
    }
    
    @Test
    void getEmployeesByNameSearch_ShouldReturnBadRequest_WhenSearchStringIsEmpty() {
        // Given
        String emptySearch = "";
        
        // When
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(emptySearch);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).getEmployeesByNameSearch(any());
    }
    
    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenServiceReturnsEmployee() {
        // Given
        String employeeId = "1";
        Employee foundEmployee = mockEmployees.get(0); // John Doe
        when(employeeService.getEmployeeById(employeeId)).thenReturn(foundEmployee);
        
        // When
        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getEmployeeName());
        assertEquals("1", response.getBody().getId());
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }
    
    @Test
    void getEmployeeById_ShouldReturnNotFound_WhenServiceReturnsNull() {
        // Given
        String employeeId = "999";
        when(employeeService.getEmployeeById(employeeId)).thenReturn(null);
        
        // When
        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }
    
    @Test
    void getEmployeeById_ShouldReturnBadRequest_WhenIdIsEmpty() {
        // Given
        String emptyId = "";
        
        // When
        ResponseEntity<Employee> response = employeeController.getEmployeeById(emptyId);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).getEmployeeById(any());
    }
}
