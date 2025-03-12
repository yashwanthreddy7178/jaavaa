package com.portal.employeeportal.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.employeeportal.config.JwtAuthenticationFilter;
import com.portal.employeeportal.config.SecurityConfig;
import com.portal.employeeportal.dto.FormRequest;
import com.portal.employeeportal.dto.FormResponse;
import com.portal.employeeportal.service.FormService;
import com.portal.employeeportal.service.JwtService;

@WebMvcTest(FormController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FormService formService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    private FormRequest testFormRequest;
    private FormResponse testFormResponse;

    @BeforeEach
    void setUp() {
        testFormRequest = FormRequest.builder()
                .formType("LEAVE_REQUEST")
                .content("{\"reason\":\"vacation\"}")
                .build();

        testFormResponse = FormResponse.builder()
                .id(1L)
                .formType("LEAVE_REQUEST")
                .status("PENDING")
                .build();
    }

    @Test
    @WithMockUser
    void submitForm_Success() throws Exception {
        when(formService.submitForm(any())).thenReturn(testFormResponse);

        mockMvc.perform(post("/api/forms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFormRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.formType").value("LEAVE_REQUEST"));
    }

    @Test
    @WithMockUser
    void getAllForms_Success() throws Exception {
        when(formService.getAllForms()).thenReturn(List.of(testFormResponse));

        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void getFormById_Success() throws Exception {
        when(formService.getFormById(1L)).thenReturn(testFormResponse);

        mockMvc.perform(get("/api/forms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void submitForm_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/forms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFormRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllForms_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void submitForm_BadRequest() throws Exception {
        FormRequest invalidRequest = FormRequest.builder().build();

        mockMvc.perform(post("/api/forms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
} 