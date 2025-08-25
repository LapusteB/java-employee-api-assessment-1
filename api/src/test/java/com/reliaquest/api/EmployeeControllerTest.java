package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeInput;
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
    
    // Helper method to create mock data for top 10 salary testing
    private List<Employee> createMockTopSalaryEmployees() {
        List<Employee> topSalaryEmployees = Arrays.asList(
            createEmployee("CEO", "Alice Johnson", 200000),
            createEmployee("CTO", "Bob Wilson", 180000),
            createEmployee("CFO", "Carol Davis", 175000),
            createEmployee("VP Engineering", "David Brown", 160000),
            createEmployee("VP Sales", "Eva Garcia", 155000),
            createEmployee("Senior Architect", "Frank Miller", 140000),
            createEmployee("Senior Manager", "Grace Lee", 135000),
            createEmployee("Lead Developer", "Henry Taylor", 130000),
            createEmployee("Product Manager", "Ivy Chen", 125000),
            createEmployee("Senior Developer", "Jack Anderson", 120000),
            createEmployee("Developer", "Kate White", 110000),  // Should be excluded (11th)
            createEmployee("Junior Developer", "Liam Clark", 90000)   // Should be excluded (12th)
        );
        return topSalaryEmployees;
    }
    
    private Employee createEmployee(String title, String name, int salary) {
        Employee emp = new Employee();
        emp.setId(String.valueOf(System.currentTimeMillis())); // Unique ID
        emp.setEmployeeName(name);
        emp.setEmployeeSalary(salary);
        emp.setEmployeeAge(25 + (int)(Math.random() * 20)); // Random age 25-45
        emp.setEmployeeTitle(title);
        emp.setEmployeeEmail(name.toLowerCase().replace(" ", ".") + "@company.com");
        return emp;
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
    
    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopTenNames_WhenServiceReturnsEmployees() {
        // Given
        List<String> expectedTopTenNames = Arrays.asList(
            "Alice Johnson", "Bob Wilson", "Carol Davis", "David Brown", "Eva Garcia",
            "Frank Miller", "Grace Lee", "Henry Taylor", "Ivy Chen", "Jack Anderson"
        );
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
        invalidInput.setName("");  // Empty name
        invalidInput.setSalary(50000);
        invalidInput.setAge(25);
        invalidInput.setTitle("Developer");
        
        // When
        ResponseEntity<Employee> response = employeeController.createEmployee(invalidInput);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(employeeService, never()).createEmployee(any());
    }
    
    // ===== DELETE EMPLOYEE TESTS =====
    
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
        String emptyId = null;
        
        // When
        ResponseEntity<String> response = employeeController.deleteEmployeeById(emptyId);
        
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
