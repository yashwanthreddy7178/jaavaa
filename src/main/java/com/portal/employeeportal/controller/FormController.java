package com.portal.employeeportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portal.employeeportal.dto.FormSubmissionRequest;
import com.portal.employeeportal.model.Form;
import com.portal.employeeportal.service.FormService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @PostMapping
    public ResponseEntity<Form> submitForm(@Valid @RequestBody FormSubmissionRequest request) {
        return ResponseEntity.ok(formService.submitForm(request));
    }

    @GetMapping
    public ResponseEntity<List<Form>> getForms() {
        return ResponseEntity.ok(formService.getEmployeeForms());
    }
} 