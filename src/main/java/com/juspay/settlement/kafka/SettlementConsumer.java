package com.juspay.settlement.kafka;

import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.service.EventsTrackerService;
import com.juspay.settlement.service.ExternalBankService;
import com.juspay.settlement.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementConsumer {

    private final ExternalBankService externalBankService;
    private final KafkaProducerService kafkaProducerService;
    private final EventsTrackerService eventsTrackerService;

    @KafkaListener(
            topics = "${settlement.topics.settlement-flow}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSettlementFlow(
            @Payload SettlementEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received settlement flow event: {} of type: {} from partition {} offset {}",
                event.getEventId(), event.getEventType(), partition, offset);

        eventsTrackerService.trackEventWithOffset(event, topic, partition, offset);

        try {
            if (SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name().equals(event.getEventType())) {
                processSettlementInstruction(event);
            } else if (SettlementEvent.EventType.SETTLEMENT_RETRY.name().equals(event.getEventType())) {
                if (event.getRetryCount() != null && event.getRetryCount() < 3) {
                    processSettlementInstruction(event);
                } else {
                    log.error("Max retries exceeded for txnId: {}", event.getTxnId());
                }
            }
        } catch (Exception e) {
            log.error("Error processing settlement event {}: {}", event.getEventId(), e.getMessage());
            eventsTrackerService.trackEventError(
                    event.getEventId(), event.getEventType(), topic,
                    event.getTxnId(), event.getReconId(), e.getMessage()
            );
        }
    }

    private void processSettlementInstruction(SettlementEvent event) {
        log.info("Processing settlement for txnId: {}", event.getTxnId());

        // Publish initiated event
        SettlementEvent initiatedEvent = SettlementEvent.builder()
                .eventId(event.getEventId())
                .eventType(SettlementEvent.EventType.SETTLEMENT_INITIATED.name())
                .txnId(event.getTxnId())
                .reconId(event.getReconId())
                .merchantId(event.getMerchantId())
                .acquirerId(event.getAcquirerId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .settlementReference(event.getSettlementReference())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        kafkaProducerService.publishSettlementStatus(initiatedEvent);

        // Call external bank API (use mock for now)
        SettlementEvent resultEvent = externalBankService.processSettlementMock(event);

        // Publish result to status topic
        kafkaProducerService.publishSettlementStatus(resultEvent);
        eventsTrackerService.trackEvent(resultEvent, "settlement-status-updates");

        log.info("Settlement processed for txnId: {} with status: {}",
                resultEvent.getTxnId(), resultEvent.getStatus());
    }
}
