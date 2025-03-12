package com.portal.employeeportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormRequest {
    @NotBlank(message = "Form type is required")
    private String formType;

    @NotNull(message = "Content is required")
    private String content;
} 