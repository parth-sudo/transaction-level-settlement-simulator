package com.juspay.settlement.model;

import jakarta.validation.constraints.NotBlank;

public class SettlementRequest {

    @NotBlank(message = "reconId is required")
    private String reconId;

    private String merchantId;
    private String acquirerId;
    private Boolean processAsync;

    public String getReconId() { return reconId; }
    public void setReconId(String reconId) { this.reconId = reconId; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getAcquirerId() { return acquirerId; }
    public void setAcquirerId(String acquirerId) { this.acquirerId = acquirerId; }

    public Boolean getProcessAsync() { return processAsync; }
    public void setProcessAsync(Boolean processAsync) { this.processAsync = processAsync; }
}
