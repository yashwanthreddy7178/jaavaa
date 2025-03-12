package com.portal.employeeportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.portal.employeeportal.dto.FormSubmissionRequest;
import com.portal.employeeportal.model.Employee;
import com.portal.employeeportal.model.Form;
import com.portal.employeeportal.repository.EmployeeRepository;
import com.portal.employeeportal.repository.FormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final EmployeeRepository employeeRepository;

    public Form submitForm(FormSubmissionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Form form = Form.builder()
                .employee(employee)
                .formType(request.getFormType())
                .content(request.getContent())
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();

        return formRepository.save(form);
    }

    public List<Form> getEmployeeForms() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return formRepository.findByEmployeeOrderBySubmittedAtDesc(employee);
    }
} 