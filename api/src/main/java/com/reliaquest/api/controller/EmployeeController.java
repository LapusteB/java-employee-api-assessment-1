package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
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
