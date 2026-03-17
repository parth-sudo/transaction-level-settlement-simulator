package com.juspay.settlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events_tracker")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventsTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic_name", nullable = false, length = 200)
    private String topicName;

    @Column(name = "partition_num")
    private Integer partitionNum;

    @Column(name = "offset_num")
    private Long offsetNum;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "recon_id")
    private String reconId;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "status", length = 50)
    @Builder.Default
    private String status = "RECEIVED";

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
