package com.juspay.settlement.service;

import com.juspay.settlement.config.SettlementProperties;
import com.juspay.settlement.model.SettlementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ExternalBankService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalBankService.class);

    private final SettlementProperties settlementProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExternalBankService(SettlementProperties settlementProperties) {
        this.settlementProperties = settlementProperties;
    }

    public SettlementEvent processSettlement(SettlementEvent event) {
        logger.info("Processing settlement for txnId: {} via external bank API", event.getTxnId());

        try {
            String url = settlementProperties.getExternalApi().getBaseUrl() +
                        settlementProperties.getExternalApi().getEndpoint();

            Map<String, Object> request = buildBankRequest(event);

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
                    SettlementEvent result = new SettlementEvent();
                    result.setEventId(UUID.randomUUID().toString());
                    result.setEventType(SettlementEvent.EventType.SETTLEMENT_SUCCESS.name());
                    result.setTxnId(event.getTxnId());
                    result.setReconId(event.getReconId());
                    result.setMerchantId(event.getMerchantId());
                    result.setAcquirerId(event.getAcquirerId());
                    result.setAmount(event.getAmount());
                    result.setCurrency(event.getCurrency());
                    result.setStatus("SETTLED");
                    result.setSettlementReference(event.getSettlementReference());
                    result.setExternalReference(reference);
                    result.setTimestamp(LocalDateTime.now());
                    return result;
                } else {
                    return buildFailureEvent(event, "Settlement rejected by bank: " + status);
                }
            } else {
                return buildFailureEvent(event, "Bank API returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error calling external bank API for txnId {}: {}", event.getTxnId(), e.getMessage());
            return buildFailureEvent(event, "API call failed: " + e.getMessage());
        }
    }

    public SettlementEvent processSettlementMock(SettlementEvent event) {
        logger.info("[MOCK] Processing settlement for txnId: {}", event.getTxnId());

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isSuccess = Math.random() > 0.05;

        if (isSuccess) {
            SettlementEvent result = new SettlementEvent();
            result.setEventId(UUID.randomUUID().toString());
            result.setEventType(SettlementEvent.EventType.SETTLEMENT_SUCCESS.name());
            result.setTxnId(event.getTxnId());
            result.setReconId(event.getReconId());
            result.setMerchantId(event.getMerchantId());
            result.setAcquirerId(event.getAcquirerId());
            result.setAmount(event.getAmount());
            result.setCurrency(event.getCurrency());
            result.setStatus("SETTLED");
            result.setSettlementReference(event.getSettlementReference());
            result.setExternalReference("BANK-REF-" + UUID.randomUUID().toString().substring(0, 8));
            result.setTimestamp(LocalDateTime.now());
            return result;
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
        SettlementEvent result = new SettlementEvent();
        result.setEventId(UUID.randomUUID().toString());
        result.setEventType(SettlementEvent.EventType.SETTLEMENT_FAILED.name());
        result.setTxnId(event.getTxnId());
        result.setReconId(event.getReconId());
        result.setMerchantId(event.getMerchantId());
        result.setAcquirerId(event.getAcquirerId());
        result.setAmount(event.getAmount());
        result.setCurrency(event.getCurrency());
        result.setStatus("FAILED");
        result.setSettlementReference(event.getSettlementReference());
        result.setRetryCount(event.getRetryCount() != null ? event.getRetryCount() + 1 : 1);
        result.setErrorMessage(errorMessage);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }
}
