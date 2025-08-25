package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetHighestSalaryTest {
    
    @Mock
    private EmployeeService employeeService;
    
    @InjectMocks
    private EmployeeController employeeController;
    
    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary_WhenServiceReturnsSalary() {
        // Given
        Integer highestSalary = 60000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);
        
        // When
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(highestSalary, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }
    
    @Test
    void getHighestSalaryOfEmployees_ShouldReturnZero_WhenServiceReturnsZero() {
        // Given
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(0);
        
        // When
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }
    
    @Test
    void getHighestSalaryOfEmployees_ShouldReturnInternalServerError_WhenServiceThrowsException() {
        // Given
        when(employeeService.getHighestSalaryOfEmployees()).thenThrow(new RuntimeException("Service error"));
        
        // When
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }
}
