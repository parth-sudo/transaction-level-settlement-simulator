package com.juspay.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementResponse {

    private String reconId;
    private String status;
    private String message;
    private Integer totalTransactions;
    private Integer processedCount;
    private Integer pendingCount;
    private Integer failedCount;
    private String batchId;
    private List<TransactionDetail> transactions;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDetail {
        private String txnId;
        private String status;
        private String settlementReference;
        private String errorMessage;
    }
}
