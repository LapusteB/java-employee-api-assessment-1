package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GetTopTenTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopTenNames_WhenServiceReturnsEmployees() {
        // Given
        List<String> expectedTopTenNames = Arrays.asList(
                "Alice Johnson",
                "Bob Wilson",
                "Carol Davis",
                "David Brown",
                "Eva Garcia",
                "Frank Miller",
                "Grace Lee",
                "Henry Taylor",
                "Ivy Chen",
                "Jack Anderson");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedTopTenNames);

        // When
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().size());
        assertEquals("Alice Johnson", response.getBody().get(0)); // Highest earner first
        assertEquals("Jack Anderson", response.getBody().get(9)); // 10th highest earner
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnLessThanTen_WhenServiceReturnsLessEmployees() {
        // Given
        List<String> lessThanTenNames = Arrays.asList("Alice Johnson", "Bob Wilson", "Carol Davis");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(lessThanTenNames);

        // When
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnEmptyList_WhenServiceReturnsEmptyList() {
        // Given
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnInternalServerError_WhenServiceThrowsException() {
        // Given
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }
}
