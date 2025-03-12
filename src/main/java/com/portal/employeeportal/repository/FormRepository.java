package com.portal.employeeportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portal.employeeportal.model.Employee;
import com.portal.employeeportal.model.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByEmployee(Employee employee);
    List<Form> findByEmployeeOrderBySubmittedAtDesc(Employee employee);
} 