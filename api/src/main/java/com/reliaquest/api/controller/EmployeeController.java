package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping
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
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@RequestParam String searchString) {
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
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
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
    @GetMapping("/highest-salary")
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
    @GetMapping("/top-ten")
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
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeInput employeeInput) {
        try {
            // Validate input
            if (employeeInput == null) {
                return ResponseEntity.badRequest().build();
            }

            if (employeeInput.getName() == null
                    || employeeInput.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (employeeInput.getSalary() == null || employeeInput.getSalary() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            if (employeeInput.getAge() == null || employeeInput.getAge() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            if (employeeInput.getTitle() == null
                    || employeeInput.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Create employee
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.ok(createdEmployee);

        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        try {
            // Validate input
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Delete employee
            String result = employeeService.deleteEmployeeById(id.trim());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Log the error (you can add proper logging here)
            return ResponseEntity.internalServerError().build();
        }
    }
}
