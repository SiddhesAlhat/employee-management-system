package com.zest.employeemanagement.repository;

import com.zest.employeemanagement.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void existsByEmail_shouldReturnTrue_whenEmployeeWithEmailExists() {
        Employee employee = Employee.builder()
                .name("Rutuja Sharma")
                .email("rutuja.test@example.com")
                .department("Engineering")
                .position("Developer")
                .salary(50000.0)
                .dateOfJoining(LocalDate.now())
                .build();

        employeeRepository.save(employee);

        assertTrue(employeeRepository.existsByEmail("rutuja.test@example.com"));
        assertFalse(employeeRepository.existsByEmail("nonexistent@example.com"));
    }
}
