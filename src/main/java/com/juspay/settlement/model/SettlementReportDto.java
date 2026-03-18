package com.juspay.settlement.model;

import java.math.BigDecimal;

public class SettlementReportDto {

    private String merchantId;
    private BigDecimal totalSettlementAmount;
    private BigDecimal totalFeesAmount;
    private BigDecimal totalTaxAmount;
    private String txnType;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getTotalSettlementAmount() {
        return totalSettlementAmount;
    }

    public void setTotalSettlementAmount(BigDecimal totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }

    public BigDecimal getTotalFeesAmount() {
        return totalFeesAmount;
    }

    public void setTotalFeesAmount(BigDecimal totalFeesAmount) {
        this.totalFeesAmount = totalFeesAmount;
    }

    public BigDecimal getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }
}
