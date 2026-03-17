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
public interface EventsTrackerRepository extends JpaRepository<EventsTracker, Long> {

    Optional<EventsTracker> findByEventId(String eventId);

    List<EventsTracker> findByTxnId(String txnId);

    List<EventsTracker> findByReconId(String reconId);

    List<EventsTracker> findByTopicName(String topicName);

    List<EventsTracker> findByEventType(String eventType);

    @Query("SELECT et FROM EventsTracker et WHERE et.createdAt BETWEEN :startTime AND :endTime")
    List<EventsTracker> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(et) FROM EventsTracker et WHERE et.reconId = :reconId")
    Long countByReconId(@Param("reconId") String reconId);

    @Query("SELECT et FROM EventsTracker et WHERE et.reconId = :reconId AND et.eventType = :eventType")
    List<EventsTracker> findByReconIdAndEventType(@Param("reconId") String reconId,
                                                   @Param("eventType") String eventType);
}
