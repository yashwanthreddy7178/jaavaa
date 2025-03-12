package com.portal.employeeportal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.employeeportal.dto.FormRequest;
import com.portal.employeeportal.model.Employee;
import com.portal.employeeportal.model.Form;
import com.portal.employeeportal.repository.EmployeeRepository;
import com.portal.employeeportal.repository.FormRepository;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FormService formService;

    private Employee testEmployee;
    private Form testForm;
    private FormRequest testFormRequest;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.builder()
                .id(1L)
                .email("test@example.com")
                .employeeId("EMP001")
                .name("Test Employee")
                .build();

        testForm = Form.builder()
                .id(1L)
                .employee(testEmployee)
                .formType("LEAVE_REQUEST")
                .content("{\"reason\":\"vacation\"}")
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();

        testFormRequest = FormRequest.builder()
                .formType("LEAVE_REQUEST")
                .content("{\"reason\":\"vacation\"}")
                .build();

        // Only set up security context for tests that need it
        // Remove from here and add in specific tests
    }

    @Test
    void submitForm_Success() {
        // Set up security context only in tests that need it
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getName()).thenReturn("test@example.com");
        when(employeeRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testEmployee));
        when(formRepository.save(any(Form.class))).thenReturn(testForm);

        var result = formService.submitForm(testFormRequest);

        assertNotNull(result);
        assertEquals("LEAVE_REQUEST", result.getFormType());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void getFormById_Success() {
        when(formRepository.findById(1L)).thenReturn(Optional.of(testForm));

        var result = formService.getFormById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllForms_Success() {
        when(formRepository.findAll()).thenReturn(List.of(testForm));

        var result = formService.getAllForms();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateForm_Success() {
        when(formRepository.findById(1L)).thenReturn(Optional.of(testForm));
        when(formRepository.save(any(Form.class))).thenReturn(testForm);

        var result = formService.updateForm(1L, testFormRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getFormById_NotFound() {
        when(formRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> formService.getFormById(1L));
    }

    @Test
    void submitForm_UserNotFound() {
        // Set up security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getName()).thenReturn("test@example.com");
        when(employeeRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> formService.submitForm(testFormRequest));
    }

    @Test
    void updateForm_NotFound() {
        when(formRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> formService.updateForm(1L, testFormRequest));
    }
} 