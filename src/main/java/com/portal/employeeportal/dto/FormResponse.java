package com.portal.employeeportal.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormResponse {
    private Long id;
    private String formType;
    private String status;
    private JsonNode content;
    private LocalDateTime submittedAt;
    private LocalDateTime lastUpdated;
    private String employeeId;
} 