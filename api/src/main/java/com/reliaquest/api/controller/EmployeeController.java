package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
// Fill out the controller class to implement the IEmployeeController interface
// The controller class is responsible for handling the HTTP requests and responses
// The controller class is annotated with @RestController to mark the class as a Spring REST controller
// The controller class implements the IEmployeeController interface to define the HTTP endpoints
// The controller class is used to handle the HTTP requests and responses for the Employee resource

@RestController
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {
    
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeInput employeeInput) {
        // TODO: Implement
        return null;
    }
    
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        // TODO: Implement
        return null;
    }
}
