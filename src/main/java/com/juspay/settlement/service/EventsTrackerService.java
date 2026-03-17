package com.juspay.settlement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juspay.settlement.entity.EventsTracker;
import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.repository.EventsTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsTrackerService {

    private final EventsTrackerRepository eventsTrackerRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void trackEvent(SettlementEvent event, String topicName) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            EventsTracker tracker = EventsTracker.builder()
                    .eventId(event.getEventId())
                    .eventType(event.getEventType())
                    .topicName(topicName)
                    .txnId(event.getTxnId())
                    .reconId(event.getReconId())
                    .payload(payload)
                    .status("PROCESSED")
                    .build();

            eventsTrackerRepository.save(tracker);
            log.debug("Tracked event {} of type {} in topic {}",
                    event.getEventId(), event.getEventType(), topicName);
        } catch (Exception e) {
            log.error("Failed to track event {}: {}", event.getEventId(), e.getMessage());
        }
    }

    @Transactional
    public void trackEventWithOffset(SettlementEvent event,
                                      String topicName,
                                      int partition,
                                      long offset) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            EventsTracker tracker = EventsTracker.builder()
                    .eventId(event.getEventId())
                    .eventType(event.getEventType())
                    .topicName(topicName)
                    .partitionNum(partition)
                    .offsetNum(offset)
                    .txnId(event.getTxnId())
                    .reconId(event.getReconId())
                    .payload(payload)
                    .status("PROCESSED")
                    .build();

            eventsTrackerRepository.save(tracker);
        } catch (Exception e) {
            log.error("Failed to track event {}: {}", event.getEventId(), e.getMessage());
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
            EventsTracker tracker = EventsTracker.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .topicName(topicName)
                    .txnId(txnId)
                    .reconId(reconId)
                    .status("ERROR")
                    .errorMessage(errorMessage)
                    .build();

            eventsTrackerRepository.save(tracker);
            log.error("Tracked error for event {}: {}", eventId, errorMessage);
        } catch (Exception e) {
            log.error("Failed to track error for event {}: {}", eventId, e.getMessage());
        }
    }
}
