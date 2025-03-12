package com.portal.employeeportal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.portal.employeeportal.dto.AuthenticationRequest;
import com.portal.employeeportal.dto.RegisterRequest;
import com.portal.employeeportal.model.Employee;
import com.portal.employeeportal.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .employeeId("EMP001")
                .password("password123")
                .build();

        authRequest = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testEmployee = Employee.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .employeeId("EMP001")
                .password("encodedPassword")
                .role("EMPLOYEE")
                .build();
    }

    @Test
    void register_Success() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(employeeRepository.save(any())).thenReturn(testEmployee);
        when(jwtService.generateToken(any())).thenReturn("testToken");

        var result = authenticationService.register(registerRequest);

        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    void authenticate_Success() {
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.of(testEmployee));
        when(jwtService.generateToken(any())).thenReturn("testToken");

        var result = authenticationService.authenticate(authRequest);

        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    void requestPasswordReset_Success() {
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any())).thenReturn(testEmployee);

        assertDoesNotThrow(() -> 
            authenticationService.requestPasswordReset("test@example.com")
        );
    }

    @Test
    void register_EmailExists() {
        when(employeeRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            authenticationService.register(registerRequest)
        );
    }

    @Test
    void register_EmployeeIdExists() {
        when(employeeRepository.existsByEmployeeId(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            authenticationService.register(registerRequest)
        );
    }

    @Test
    void requestPasswordReset_UserNotFound() {
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            authenticationService.requestPasswordReset("test@example.com")
        );
    }
} 