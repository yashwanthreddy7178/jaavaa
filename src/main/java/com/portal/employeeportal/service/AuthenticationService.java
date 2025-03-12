package com.portal.employeeportal.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.portal.employeeportal.dto.AuthenticationRequest;
import com.portal.employeeportal.dto.AuthenticationResponse;
import com.portal.employeeportal.dto.RegisterRequest;
import com.portal.employeeportal.model.Employee;
import com.portal.employeeportal.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        var employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .employeeId(request.getEmployeeId())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("EMPLOYEE")
                .build();
        
        employeeRepository.save(employee);
        var jwtToken = jwtService.generateToken(employee.getEmail());
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(employee.getEmail());
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void requestPasswordReset(String email) {
        var employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String token = UUID.randomUUID().toString();
        employee.setResetToken(token);
        employee.setResetTokenExpiry(Instant.now().plusSeconds(3600).getEpochSecond()); // 1 hour expiry
        
        employeeRepository.save(employee);
        emailService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        var employee = employeeRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));

        if (employee.getResetTokenExpiry() < Instant.now().getEpochSecond()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setResetToken(null);
        employee.setResetTokenExpiry(null);
        
        employeeRepository.save(employee);
    }
} 