package com.juspay.settlement.controller;

import com.juspay.settlement.model.SettlementReportDto;
import com.juspay.settlement.model.SettlementRequest;
import com.juspay.settlement.model.SettlementResponse;
import com.juspay.settlement.service.SettlementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlement")
public class SettlementController {

    private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<SettlementResponse> initiateSettlement(
            @Valid @RequestBody SettlementRequest request) {
        logger.info("Received settlement initiation request for reconId: {}", request.getReconId());
        SettlementResponse response = settlementService.initiateSettlement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{entityId}")
    public ResponseEntity<SettlementResponse> getSettlementStatus(
            @PathVariable String entityId) {
        logger.info("Received status check request for entityId: {}", entityId);
        SettlementResponse response = settlementService.getSettlementStatus(entityId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process_batch/{reconId}")
    public ResponseEntity<SettlementResponse> processBatch(@PathVariable String reconId) {
        logger.info("Received batch process request for reconId: {}", reconId);
        SettlementRequest request = new SettlementRequest();
        request.setReconId(reconId);
        SettlementResponse response = settlementService.initiateSettlement(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process_per_txn")
    public ResponseEntity<SettlementResponse> processPerTxn(
            @RequestParam String txn_id) {
        logger.info("Received single transaction process request for txn_id: {}", txn_id);
        SettlementResponse response = settlementService.processSingleTransaction(txn_id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Settlement Service is running");
    }

    @PostMapping("/fund_transfer/{parentSettlementId}")
    public ResponseEntity<SettlementResponse> queueSettlements(@PathVariable String parentSettlementId) {
        logger.info("Received queue request for parentSettlementId: {}", parentSettlementId);
        SettlementResponse response = settlementService.queueSettlementsByParentId(parentSettlementId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{entityId}")
    public ResponseEntity<List<SettlementReportDto>> getConsolidatedReport(@PathVariable String entityId) {
        logger.info("Received report request for entityId: {}", entityId);
        List<SettlementReportDto> report = settlementService.getConsolidatedReport(entityId);
        return ResponseEntity.ok(report);
    }
}
