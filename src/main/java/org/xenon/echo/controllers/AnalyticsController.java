package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.dtos.AdminSystemAnalyticsDto;
import org.xenon.echo.services.AnalyticsService;

@Tag(name = "Analytics")
@RestController
@AllArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    @GetMapping("/admin/system/analytics")
    public ResponseEntity<AdminSystemAnalyticsDto> getAdminSystemAnalytics(){
        return ResponseEntity.ok(analyticsService.getSystemAnalytics());
    }
}
