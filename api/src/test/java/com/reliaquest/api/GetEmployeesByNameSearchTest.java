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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEmployeesByNameSearchTest {
    
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
}
