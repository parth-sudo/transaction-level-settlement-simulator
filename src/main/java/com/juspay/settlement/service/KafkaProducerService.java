package com.juspay.settlement.service;

import com.juspay.settlement.config.SettlementProperties;
import com.juspay.settlement.model.SettlementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, SettlementEvent> kafkaTemplate;
    private final SettlementProperties settlementProperties;

    public KafkaProducerService(KafkaTemplate<String, SettlementEvent> kafkaTemplate,
                                SettlementProperties settlementProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.settlementProperties = settlementProperties;
    }

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
                logger.error("Failed to publish event {} to topic {}: {}",
                        event.getEventId(), topic, ex.getMessage());
            } else {
                logger.debug("Published event {} to topic {} partition {} offset {}",
                        event.getEventId(),
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
