package com.example.githubaccess.controller;

import com.example.githubaccess.dto.AccessReportResponse;
import com.example.githubaccess.service.AccessReportService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orgs")
@Validated
public class AccessReportController {

    private final AccessReportService accessReportService;

    public AccessReportController(AccessReportService accessReportService) {
        this.accessReportService = accessReportService;
    }

    @GetMapping("/{organization}/access-report")
    public AccessReportResponse getAccessReport(@PathVariable @NotBlank String organization) {
        return accessReportService.buildAccessReport(organization);
    }
}
