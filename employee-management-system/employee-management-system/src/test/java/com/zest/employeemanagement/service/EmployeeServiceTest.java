package com.zest.employeemanagement.service;

import com.zest.employeemanagement.entity.Employee;
import com.zest.employeemanagement.exception.DuplicateResourceException;
import com.zest.employeemanagement.exception.ResourceNotFoundException;
import com.zest.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .name("Rutuja Sharma")
                .email("rutuja@example.com")
                .department("Engineering")
                .position("Software Developer")
                .salary(60000.0)
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .build();
    }

    @Test
    void createEmployee_shouldSaveAndReturnEmployee_whenEmailIsUnique() {
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertNotNull(result);
        assertEquals("Rutuja Sharma", result.getName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void createEmployee_shouldThrowException_whenEmailAlreadyExists() {
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(employee));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee_whenExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals(employee.getId(), result.getId());
    }

    @Test
    void getEmployeeById_shouldThrowException_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void updateEmployee_shouldUpdateFields_whenEmployeeExists() {
        Employee updated = Employee.builder()
                .name("Rutuja S.")
                .email("rutuja.updated@example.com")
                .department("Product")
                .position("Senior Developer")
                .salary(75000.0)
                .dateOfJoining(LocalDate.of(2024, 2, 1))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee result = employeeService.updateEmployee(1L, updated);

        assertEquals("Rutuja S.", result.getName());
        assertEquals("Product", result.getDepartment());
        assertEquals(75000.0, result.getSalary());
    }

    @Test
    void deleteEmployee_shouldRemoveEmployee_whenExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployee_shouldThrowException_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(99L));
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
