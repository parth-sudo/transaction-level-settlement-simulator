package com.juspay.settlement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "events_tracker", schema = "recon")
public class EventsTracker {

    @Id
    @Column(name = "event_id", length = 128, nullable = false)
    private String eventId;

    @Column(name = "event_name", length = 255)
    private String eventName;

    @Column(name = "event_type", length = 255)
    private String eventType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private String metadata;

    @Column(name = "event_created_by", length = 255, nullable = false)
    private String eventCreatedBy;

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Column(name = "payment_entity", length = 50, nullable = false)
    private String paymentEntity;

    @Column(name = "event_status", length = 64)
    private String eventStatus;

    @Column(name = "sub_entity_id", length = 128)
    private String subEntityId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getEventCreatedBy() { return eventCreatedBy; }
    public void setEventCreatedBy(String eventCreatedBy) { this.eventCreatedBy = eventCreatedBy; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getPaymentEntity() { return paymentEntity; }
    public void setPaymentEntity(String paymentEntity) { this.paymentEntity = paymentEntity; }

    public String getEventStatus() { return eventStatus; }
    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }

    public String getSubEntityId() { return subEntityId; }
    public void setSubEntityId(String subEntityId) { this.subEntityId = subEntityId; }
}
