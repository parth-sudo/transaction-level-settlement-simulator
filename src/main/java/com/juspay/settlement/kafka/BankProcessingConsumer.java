package com.juspay.settlement.kafka;

import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.repository.SettlementInstructionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class BankProcessingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BankProcessingConsumer.class);

    private final SettlementInstructionRepository settlementInstructionRepository;
    private final RestTemplate restTemplate;

    @Value("${settlement.external-api.base-url:http://localhost:8080}")
    private String bankApiBaseUrl;

    @Value("${settlement.external-api.endpoint:/api/v1/settle}")
    private String bankApiEndpoint;

    public BankProcessingConsumer(SettlementInstructionRepository settlementInstructionRepository,
                                   RestTemplate restTemplate) {
        this.settlementInstructionRepository = settlementInstructionRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
            topics = "${settlement.topics.bank-processing}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void processBankSettlement(
            @Payload SettlementEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Received bank processing event: {} for txnId: {} from partition {} offset {}",
                event.getEventId(), event.getTxnId(), partition, offset);

        try {
            // Call dummy bank API
            boolean bankSuccess = callBankApi(event);

            if (bankSuccess) {
                // Update status to SUCCESS
                settlementInstructionRepository.updateStatusByTxnIdentifier(
                        event.getTxnId(),
                        "SUCCESS",
                        "Bank settlement completed"
                );
                logger.info("Settlement SUCCESS for txnId: {}", event.getTxnId());
            } else {
                // Update status to FAILED
                settlementInstructionRepository.updateStatusByTxnIdentifier(
                        event.getTxnId(),
                        "FAILED",
                        "Bank settlement failed"
                );
                logger.error("Settlement FAILED for txnId: {}", event.getTxnId());
            }
        } catch (Exception e) {
            logger.error("Error processing bank settlement for txnId: {}", event.getTxnId(), e);
            settlementInstructionRepository.updateStatusByTxnIdentifier(
                    event.getTxnId(),
                    "FAILED",
                    "Error: " + e.getMessage()
            );
        }
    }

    private boolean callBankApi(SettlementEvent event) {
        try {
            String url = bankApiBaseUrl + bankApiEndpoint;

            Map<String, Object> request = new HashMap<>();
            request.put("txnId", event.getTxnId());
            request.put("merchantId", event.getMerchantId());
            request.put("amount", event.getAmount());
            request.put("currency", event.getCurrency());
            request.put("settlementReference", event.getSettlementReference());

            logger.debug("Calling bank API: {} with payload: {}", url, request);

            // For now, simulate a successful bank response
            // In production, this would be: restTemplate.postForEntity(url, request, String.class);

            // Simulate bank API call - 90% success rate
            boolean success = Math.random() > 0.1;

            logger.info("Bank API response for txnId: {} - {}", event.getTxnId(), success ? "SUCCESS" : "FAILED");
            return success;

        } catch (Exception e) {
            logger.error("Bank API call failed for txnId: {}", event.getTxnId(), e);
            return false;
        }
    }
}