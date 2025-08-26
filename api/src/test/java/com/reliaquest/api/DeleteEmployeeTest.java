package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DeleteEmployeeTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void deleteEmployeeById_ShouldReturnSuccessMessage_WhenServiceDeletesEmployee() {
        // Given
        String employeeId = "123";
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn("Employee deleted successfully");

        // When
        ResponseEntity<String> response = employeeController.deleteEmployeeById(employeeId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee deleted successfully", response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }

    @Test
    void deleteEmployeeById_ShouldReturnBadRequest_WhenIdIsEmpty() {
        // Given
        String emptyId = "";

        // When
        ResponseEntity<String> response = employeeController.deleteEmployeeById(emptyId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldReturnBadRequest_WhenIdIsNull() {
        // Given
        String nullId = null;

        // When
        ResponseEntity<String> response = employeeController.deleteEmployeeById(nullId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeById_ShouldReturnInternalServerError_WhenServiceThrowsException() {
        // Given
        String employeeId = "123";
        when(employeeService.deleteEmployeeById(employeeId)).thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<String> response = employeeController.deleteEmployeeById(employeeId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }
}
