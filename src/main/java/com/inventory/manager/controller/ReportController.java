package com.inventory.manager.controller;

import com.inventory.manager.dto.StockReportDTO;
import com.inventory.manager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/stock")
    public ResponseEntity<List<StockReportDTO>> getStockReport() {
        return ResponseEntity.ok(reportService.generateStockReport());
    }
    
    @GetMapping("/stock/location/{locationId}")
    public ResponseEntity<List<StockReportDTO>> getStockReportByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(reportService.generateStockReportByLocation(locationId));
    }
}
