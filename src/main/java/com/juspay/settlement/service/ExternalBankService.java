package com.juspay.settlement.service;

import com.juspay.settlement.config.SettlementProperties;
import com.juspay.settlement.model.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalBankService {

    private final SettlementProperties settlementProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public SettlementEvent processSettlement(SettlementEvent event) {
        log.info("Processing settlement for txnId: {} via external bank API", event.getTxnId());

        try {
            String url = settlementProperties.getExternalApi().getBaseUrl() +
                        settlementProperties.getExternalApi().getEndpoint();

            Map<String, Object> request = buildBankRequest(event);

            // Call external Morpheus API (mock implementation)
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String status = responseBody != null && responseBody.get("status") != null
                        ? responseBody.get("status").toString()
                        : "SUCCESS";

                String reference = responseBody != null && responseBody.get("referenceId") != null
                        ? responseBody.get("referenceId").toString()
                        : "REF-" + UUID.randomUUID();

                if ("SUCCESS".equalsIgnoreCase(status) || "SETTLED".equalsIgnoreCase(status)) {
                    return SettlementEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType(SettlementEvent.EventType.SETTLEMENT_SUCCESS.name())
                            .txnId(event.getTxnId())
                            .reconId(event.getReconId())
                            .merchantId(event.getMerchantId())
                            .acquirerId(event.getAcquirerId())
                            .amount(event.getAmount())
                            .currency(event.getCurrency())
                            .status("SETTLED")
                            .settlementReference(event.getSettlementReference())
                            .externalReference(reference)
                            .timestamp(LocalDateTime.now())
                            .build();
                } else {
                    return buildFailureEvent(event, "Settlement rejected by bank: " + status);
                }
            } else {
                return buildFailureEvent(event, "Bank API returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling external bank API for txnId {}: {}", event.getTxnId(), e.getMessage());
            return buildFailureEvent(event, "API call failed: " + e.getMessage());
        }
    }

    // Mock method for development/testing without actual external API
    public SettlementEvent processSettlementMock(SettlementEvent event) {
        log.info("[MOCK] Processing settlement for txnId: {}", event.getTxnId());

        // Simulate some processing time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 95% success rate
        boolean isSuccess = Math.random() > 0.05;

        if (isSuccess) {
            return SettlementEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(SettlementEvent.EventType.SETTLEMENT_SUCCESS.name())
                    .txnId(event.getTxnId())
                    .reconId(event.getReconId())
                    .merchantId(event.getMerchantId())
                    .acquirerId(event.getAcquirerId())
                    .amount(event.getAmount())
                    .currency(event.getCurrency())
                    .status("SETTLED")
                    .settlementReference(event.getSettlementReference())
                    .externalReference("BANK-REF-" + UUID.randomUUID().toString().substring(0, 8))
                    .timestamp(LocalDateTime.now())
                    .build();
        } else {
            return buildFailureEvent(event, "Mock bank rejection - insufficient funds");
        }
    }

    private Map<String, Object> buildBankRequest(SettlementEvent event) {
        Map<String, Object> request = new HashMap<>();
        request.put("txnId", event.getTxnId());
        request.put("merchantId", event.getMerchantId());
        request.put("acquirerId", event.getAcquirerId());
        request.put("amount", event.getAmount());
        request.put("currency", event.getCurrency());
        request.put("batchId", event.getSettlementReference());
        request.put("settlementDate", LocalDateTime.now().toString());
        return request;
    }

    private SettlementEvent buildFailureEvent(SettlementEvent event, String errorMessage) {
        return SettlementEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(SettlementEvent.EventType.SETTLEMENT_FAILED.name())
                .txnId(event.getTxnId())
                .reconId(event.getReconId())
                .merchantId(event.getMerchantId())
                .acquirerId(event.getAcquirerId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .status("FAILED")
                .settlementReference(event.getSettlementReference())
                .retryCount(event.getRetryCount() != null ? event.getRetryCount() + 1 : 1)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
