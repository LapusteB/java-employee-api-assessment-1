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
class GetEmployeeByIdTest {
    
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
