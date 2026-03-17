package com.juspay.settlement.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

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
    private String txnId;
    private String settlementId;
    private List<TransactionDetail> transactions;
    private LocalDateTime timestamp;

    public String getReconId() { return reconId; }
    public void setReconId(String reconId) { this.reconId = reconId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Integer getProcessedCount() { return processedCount; }
    public void setProcessedCount(Integer processedCount) { this.processedCount = processedCount; }

    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }

    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }

    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }

    public List<TransactionDetail> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDetail> transactions) { this.transactions = transactions; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static class TransactionDetail {
        private String txnId;
        private String status;
        private String settlementReference;
        private String errorMessage;

        public String getTxnId() { return txnId; }
        public void setTxnId(String txnId) { this.txnId = txnId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getSettlementReference() { return settlementReference; }
        public void setSettlementReference(String settlementReference) { this.settlementReference = settlementReference; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
