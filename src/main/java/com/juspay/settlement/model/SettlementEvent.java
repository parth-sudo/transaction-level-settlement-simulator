package com.juspay.settlement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementEvent {

    private String eventId;
    private String eventType;
    private String txnId;
    private String reconId;
    private String merchantId;
    private String acquirerId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String settlementReference;
    private String externalReference;
    private Integer retryCount;
    private String errorMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public enum EventType {
        TXN_INSTRUCTION_CREATED,
        TXN_INSTRUCTION_UPDATED,
        SETTLEMENT_INITIATED,
        SETTLEMENT_SUCCESS,
        SETTLEMENT_FAILED,
        SETTLEMENT_RETRY,
        BATCH_PROCESSING_STARTED,
        BATCH_PROCESSING_COMPLETED
    }
}
