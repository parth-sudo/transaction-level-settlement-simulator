package com.juspay.settlement.controller;

import com.juspay.settlement.model.SettlementRequest;
import com.juspay.settlement.model.SettlementResponse;
import com.juspay.settlement.service.SettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
@Slf4j
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/initiate")
    public ResponseEntity<SettlementResponse> initiateSettlement(
            @Valid @RequestBody SettlementRequest request) {
        log.info("Received settlement initiation request for reconId: {}", request.getReconId());
        SettlementResponse response = settlementService.initiateSettlement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{reconId}")
    public ResponseEntity<SettlementResponse> getSettlementStatus(
            @PathVariable String reconId) {
        log.info("Received status check request for reconId: {}", reconId);
        SettlementResponse response = settlementService.getSettlementStatus(reconId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process/{reconId}")
    public ResponseEntity<SettlementResponse> processBatch(
            @PathVariable String reconId,
            @RequestParam(defaultValue = "false") boolean async) {
        log.info("Received batch process request for reconId: {}, async: {}", reconId, async);
        SettlementRequest request = SettlementRequest.builder()
                .reconId(reconId)
                .processAsync(async)
                .build();
        SettlementResponse response = settlementService.initiateSettlement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Settlement Service is running");
    }
}
