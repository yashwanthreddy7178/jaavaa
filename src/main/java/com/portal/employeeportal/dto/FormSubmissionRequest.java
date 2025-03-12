package com.portal.employeeportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionRequest {
    @NotBlank(message = "Form type is required")
    private String formType;

    @NotBlank(message = "Content is required")
    private String content;
} 