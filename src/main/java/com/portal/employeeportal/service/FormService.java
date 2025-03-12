package com.portal.employeeportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.employeeportal.dto.FormRequest;
import com.portal.employeeportal.dto.FormResponse;
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
    private final ObjectMapper objectMapper;

    public FormResponse submitForm(FormRequest request) {
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
        return mapToResponse(formRepository.save(form));
    }

    public List<FormResponse> getAllForms() {
        return formRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FormResponse getFormById(Long id) {
        return formRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Form not found"));
    }

    public FormResponse updateForm(Long id, FormRequest request) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        form.setContent(request.getContent().toString());
        form.setLastUpdated(LocalDateTime.now());
        return mapToResponse(formRepository.save(form));
    }

    private FormResponse mapToResponse(Form form) {
        JsonNode contentNode;
        try {
            contentNode = objectMapper.readTree(form.getContent());
        } catch (Exception e) {
            contentNode = null;
        }
        
        return FormResponse.builder()
                .id(form.getId())
                .formType(form.getFormType())
                .status(form.getStatus())
                .content(contentNode)
                .submittedAt(form.getSubmittedAt())
                .lastUpdated(form.getLastUpdated())
                .employeeId(form.getEmployee().getEmployeeId())
                .build();
    }
} 