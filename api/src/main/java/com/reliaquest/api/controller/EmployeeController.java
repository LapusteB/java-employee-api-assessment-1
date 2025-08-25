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
        try {
            if (searchString == null || searchString.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString.trim());
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Employee employee = employeeService.getEmployeeById(id.trim());
            
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
            return ResponseEntity.ok(highestSalary);
        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
            return ResponseEntity.ok(topTenNames);
        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
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
