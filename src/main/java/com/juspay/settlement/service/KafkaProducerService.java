package com.juspay.settlement.service;

import com.juspay.settlement.config.SettlementProperties;
import com.juspay.settlement.model.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, SettlementEvent> kafkaTemplate;
    private final SettlementProperties settlementProperties;

    public void publishTxnInstruction(SettlementEvent event) {
        String topic = settlementProperties.getTopics().getTransactionInstructions();
        publishEvent(topic, event.getTxnId(), event);
    }

    public void publishSettlementFlow(SettlementEvent event) {
        String topic = settlementProperties.getTopics().getSettlementFlow();
        String key = event.getReconId() != null ? event.getReconId() : event.getEventId();
        publishEvent(topic, key, event);
    }

    public void publishSettlementStatus(SettlementEvent event) {
        String topic = settlementProperties.getTopics().getSettlementStatus();
        publishEvent(topic, event.getTxnId(), event);
    }

    private void publishEvent(String topic, String key, SettlementEvent event) {
        CompletableFuture<SendResult<String, SettlementEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event {} to topic {}: {}",
                        event.getEventId(), topic, ex.getMessage());
            } else {
                log.debug("Published event {} to topic {} partition {} offset {}",
                        event.getEventId(),
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
