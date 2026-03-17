package com.juspay.settlement.kafka;

import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.service.EventsTrackerService;
import com.juspay.settlement.service.ExternalBankService;
import com.juspay.settlement.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SettlementConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SettlementConsumer.class);

    private final ExternalBankService externalBankService;
    private final KafkaProducerService kafkaProducerService;
    private final EventsTrackerService eventsTrackerService;

    public SettlementConsumer(ExternalBankService externalBankService,
                              KafkaProducerService kafkaProducerService,
                              EventsTrackerService eventsTrackerService) {
        this.externalBankService = externalBankService;
        this.kafkaProducerService = kafkaProducerService;
        this.eventsTrackerService = eventsTrackerService;
    }

    @KafkaListener(
            topics = "${settlement.topics.settlement-flow}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeSettlementFlow(
            @Payload SettlementEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Received settlement flow event: {} of type: {} from partition {} offset {}",
                event.getEventId(), event.getEventType(), partition, offset);

        eventsTrackerService.trackEventWithOffset(event, topic, partition, offset);

        try {
            if (SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name().equals(event.getEventType())) {
                processSettlementInstruction(event);
            } else if (SettlementEvent.EventType.SETTLEMENT_RETRY.name().equals(event.getEventType())) {
                if (event.getRetryCount() != null && event.getRetryCount() < 3) {
                    processSettlementInstruction(event);
                } else {
                    logger.error("Max retries exceeded for txnId: {}", event.getTxnId());
                }
            }
        } catch (Exception e) {
            logger.error("Error processing settlement event {}: {}", event.getEventId(), e.getMessage());
            eventsTrackerService.trackEventError(
                    event.getEventId(), event.getEventType(), topic,
                    event.getTxnId(), event.getReconId(), e.getMessage()
            );
        }
    }

    private void processSettlementInstruction(SettlementEvent event) {
        logger.info("Processing settlement for txnId: {}", event.getTxnId());

        SettlementEvent initiatedEvent = new SettlementEvent();
        initiatedEvent.setEventId(UUID.randomUUID().toString());
        initiatedEvent.setEventType(SettlementEvent.EventType.SETTLEMENT_INITIATED.name());
        initiatedEvent.setTxnId(event.getTxnId());
        initiatedEvent.setReconId(event.getReconId());
        initiatedEvent.setMerchantId(event.getMerchantId());
        initiatedEvent.setAcquirerId(event.getAcquirerId());
        initiatedEvent.setAmount(event.getAmount());
        initiatedEvent.setCurrency(event.getCurrency());
        initiatedEvent.setSettlementReference(event.getSettlementReference());
        initiatedEvent.setTimestamp(LocalDateTime.now());

        kafkaProducerService.publishSettlementStatus(initiatedEvent);

        SettlementEvent resultEvent = externalBankService.processSettlementMock(event);

        kafkaProducerService.publishSettlementStatus(resultEvent);
        eventsTrackerService.trackEvent(resultEvent, "settlement-status-updates");

        logger.info("Settlement processed for txnId: {} with status: {}",
                resultEvent.getTxnId(), resultEvent.getStatus());
    }
}
