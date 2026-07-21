package com.zest.employeemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zest.employeemanagement.entity.Employee;
import com.zest.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    // Security-related beans required by the security filter chain during context load
    @MockBean
    private com.zest.employeemanagement.security.JwtUtil jwtUtil;

    @MockBean
    private com.zest.employeemanagement.service.UserDetailsServiceImpl userDetailsService;

    private Employee sampleEmployee() {
        return Employee.builder()
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
    @WithMockUser
    void createEmployee_shouldReturn201_whenValidRequest() throws Exception {
        Employee employee = sampleEmployee();
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rutuja Sharma"));
    }

    @Test
    @WithMockUser
    void createEmployee_shouldReturn400_whenNameIsBlank() throws Exception {
        Employee invalid = sampleEmployee();
        invalid.setName("");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllEmployees_shouldReturnPagedResults() throws Exception {
        Page<Employee> page = new PageImpl<>(Collections.singletonList(sampleEmployee()));
        when(employeeService.getAllEmployees(any())).thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Rutuja Sharma"));
    }

    @Test
    @WithMockUser
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        when(employeeService.getEmployeeById(eq(1L))).thenReturn(sampleEmployee());

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rutuja@example.com"));
    }

    @Test
    void getAllEmployees_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void deleteEmployee_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
}
