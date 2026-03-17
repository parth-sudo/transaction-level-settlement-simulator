package com.juspay.settlement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juspay.settlement.entity.EventsTracker;
import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.repository.EventsTrackerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventsTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(EventsTrackerService.class);

    private final EventsTrackerRepository eventsTrackerRepository;
    private final ObjectMapper objectMapper;

    public EventsTrackerService(EventsTrackerRepository eventsTrackerRepository,
                                ObjectMapper objectMapper) {
        this.eventsTrackerRepository = eventsTrackerRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void trackEvent(SettlementEvent event, String topicName) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            EventsTracker tracker = new EventsTracker();
            tracker.setEventId(event.getEventId());
            tracker.setEventName(topicName);
            tracker.setEventType(event.getEventType());
            tracker.setEventStatus("PROCESSED");
            tracker.setEventCreatedBy("settlement-service");
            tracker.setEntityId(event.getMerchantId());
            tracker.setPaymentEntity(event.getAcquirerId() != null ? event.getAcquirerId() : "UNKNOWN");
            tracker.setMetadata(payload);
            eventsTrackerRepository.save(tracker);
            logger.debug("Tracked event {} of type {} in topic {}",
                    event.getEventId(), event.getEventType(), topicName);
        } catch (Exception e) {
            logger.error("Failed to track event {}: {}", event.getEventId(), e.getMessage());
        }
    }

    @Transactional
    public void trackEventWithOffset(SettlementEvent event,
                                      String topicName,
                                      int partition,
                                      long offset) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            EventsTracker tracker = new EventsTracker();
            tracker.setEventId(event.getEventId());
            tracker.setEventName(topicName);
            tracker.setEventType(event.getEventType());
            tracker.setEventStatus("PROCESSED");
            tracker.setEventCreatedBy("settlement-service");
            tracker.setEntityId(event.getMerchantId());
            tracker.setPaymentEntity(event.getAcquirerId() != null ? event.getAcquirerId() : "UNKNOWN");
            tracker.setMetadata(payload);

            eventsTrackerRepository.save(tracker);
        } catch (Exception e) {
            logger.error("Failed to track event {}: {}", event.getEventId(), e.getMessage());
        }
    }

    @Transactional
    public void trackEventError(String eventId,
                                 String eventType,
                                 String topicName,
                                 String txnId,
                                 String reconId,
                                 String errorMessage) {
        try {
            EventsTracker tracker = new EventsTracker();
            tracker.setEventId(eventId);
            tracker.setEventName(topicName);
            tracker.setEventType(eventType);
            tracker.setEventStatus("ERROR");
            tracker.setEventCreatedBy("settlement-service");
            tracker.setMetadata("{\"error\":\"" + errorMessage + "\",\"txnId\":\"" + txnId + "\",\"reconId\":\"" + reconId + "\"}");
            tracker.setPaymentEntity("UNKNOWN");

            eventsTrackerRepository.save(tracker);
            logger.error("Tracked error for event {}: {}", eventId, errorMessage);
        } catch (Exception e) {
            logger.error("Failed to track error for event {}: {}", eventId, e.getMessage());
        }
    }
}
