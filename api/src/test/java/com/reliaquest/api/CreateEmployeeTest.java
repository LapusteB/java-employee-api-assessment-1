package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreateEmployeeTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenServiceReturnsEmployee() {
        // Given
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("New Employee");
        input.setSalary(75000);
        input.setAge(32);
        input.setTitle("Software Engineer");

        Employee createdEmployee = new Employee();
        createdEmployee.setId("new-id-123");
        createdEmployee.setEmployeeName("New Employee");
        createdEmployee.setEmployeeSalary(75000);
        createdEmployee.setEmployeeAge(32);
        createdEmployee.setEmployeeTitle("Software Engineer");
        createdEmployee.setEmployeeEmail("new.employee@company.com");

        when(employeeService.createEmployee(input)).thenReturn(createdEmployee);

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Employee", response.getBody().getEmployeeName());
        assertEquals("new-id-123", response.getBody().getId());
        assertEquals(75000, response.getBody().getEmployeeSalary());
        verify(employeeService, times(1)).createEmployee(input);
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenInputIsNull() {
        // Given
        CreateEmployeeInput nullInput = null;

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(nullInput);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenInputNameIsEmpty() {
        // Given
        CreateEmployeeInput invalidInput = new CreateEmployeeInput();
        invalidInput.setName("");
        invalidInput.setSalary(50000);
        invalidInput.setAge(25);
        invalidInput.setTitle("Developer");

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(invalidInput);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenInputSalaryIsNegative() {
        // Given
        CreateEmployeeInput invalidInput = new CreateEmployeeInput();
        invalidInput.setName("Test Employee");
        invalidInput.setSalary(-1000);
        invalidInput.setAge(25);
        invalidInput.setTitle("Developer");

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(invalidInput);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    void createEmployee_ShouldReturnInternalServerError_WhenServiceThrowsException() {
        // Given
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Test Employee");
        input.setSalary(50000);
        input.setAge(25);
        input.setTitle("Developer");

        when(employeeService.createEmployee(input)).thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).createEmployee(input);
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenInputIsInvalid() {
        // Given
        CreateEmployeeInput invalidInput = new CreateEmployeeInput();
        invalidInput.setName(""); // Empty name
        invalidInput.setSalary(50000);
        invalidInput.setAge(25);
        invalidInput.setTitle("Developer");

        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(invalidInput);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).createEmployee(any());
    }
}
