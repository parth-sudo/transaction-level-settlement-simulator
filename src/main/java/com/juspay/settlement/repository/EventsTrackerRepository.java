package com.juspay.settlement.repository;

import com.juspay.settlement.entity.EventsTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsTrackerRepository extends JpaRepository<EventsTracker, String> {

    Optional<EventsTracker> findByEventId(String eventId);

    List<EventsTracker> findByEntityId(String entityId);

    List<EventsTracker> findByPaymentEntity(String paymentEntity);

    List<EventsTracker> findByEventType(String eventType);

    List<EventsTracker> findByEventStatus(String eventStatus);

    @Query("SELECT et FROM EventsTracker et WHERE et.createdAt BETWEEN :startTime AND :endTime")
    List<EventsTracker> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(et) FROM EventsTracker et WHERE et.entityId = :entityId")
    Long countByEntityId(@Param("entityId") String entityId);

    @Query("SELECT et FROM EventsTracker et WHERE et.entityId = :entityId AND et.eventType = :eventType")
    List<EventsTracker> findByEntityIdAndEventType(@Param("entityId") String entityId,
                                                   @Param("eventType") String eventType);
}
