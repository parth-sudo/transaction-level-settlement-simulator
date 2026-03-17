package com.juspay.settlement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_report")
public class SettlementReport {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private String id;

    @Column(name = "sys_a_txn_id", length = 128)
    private String sysATxnId;

    @Column(name = "sys_b_txn_id", length = 128)
    private String sysBTxnId;

    @Column(name = "settlement_id", length = 128)
    private String settlementId;

    @Column(name = "recon_id", length = 50)
    private String reconId;

    @Column(name = "utr_no", length = 50)
    private String utrNo;

    @Column(name = "entity_id", length = 128)
    private String entityId;

    @Column(name = "sub_entity_id", length = 128)
    private String subEntityId;

    @Column(name = "payment_entity", length = 128)
    private String paymentEntity;

    @Column(name = "payment_sub_entity", length = 128)
    private String paymentSubEntity;

    @Column(name = "pmt", length = 128)
    private String pmt;

    @Column(name = "txn_type", length = 20, nullable = false)
    private String txnType;

    @Column(name = "txn_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal txnAmount;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal feeAmount;

    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "hold_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal holdAmount;

    @Column(name = "settlement_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal settlementAmount;

    @Column(name = "txn_currency", nullable = false)
    private Integer txnCurrency = 365;

    @Column(name = "settlement_currency", nullable = false)
    private Integer settlementCurrency = 365;

    @Column(name = "txn_date")
    private LocalDateTime txnDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "udf1", length = 128)
    private String udf1;

    @Column(name = "udf2", length = 128)
    private String udf2;

    @Column(name = "udf3", length = 128)
    private String udf3;

    @Column(name = "udf4", length = 128)
    private String udf4;

    @Column(name = "udf5", length = 128)
    private String udf5;

    @Column(name = "udf6", precision = 15, scale = 4)
    private BigDecimal udf6;

    @Column(name = "udf7", precision = 15, scale = 4)
    private BigDecimal udf7;

    @Column(name = "udf8", precision = 15, scale = 4)
    private BigDecimal udf8;

    @Column(name = "udf9", length = 255)
    private String udf9;

    @Column(name = "udf10", length = 255)
    private String udf10;

    @Column(name = "hold_status", length = 20)
    private String holdStatus;

    @Column(name = "fee_mode", length = 50, nullable = false)
    private String feeMode;

    @Column(name = "mdr_fees", nullable = false, precision = 24, scale = 9)
    private BigDecimal mdrFees = BigDecimal.ZERO;

    @Column(name = "platform_fees", nullable = false, precision = 24, scale = 9)
    private BigDecimal platformFees = BigDecimal.ZERO;

    @Column(name = "others_fees", nullable = false, precision = 24, scale = 9)
    private BigDecimal othersFees = BigDecimal.ZERO;

    @Column(name = "refund_id", length = 128)
    private String refundId;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "order_id", length = 128)
    private String orderId;

    @Column(name = "dispute_id", length = 128)
    private String disputeId;

    @Column(name = "dispute_status", length = 128)
    private String disputeStatus;

    @Column(name = "dispute_type", length = 128)
    private String disputeType;

    @Column(name = "settlement_type", length = 128)
    private String settlementType;

    @Column(name = "referrer_id", length = 50)
    private String referrerId;

    @Column(name = "referrer_fees", precision = 20, scale = 4)
    private BigDecimal referrerFees;

    @Column(name = "referrer_tax", precision = 20, scale = 4)
    private BigDecimal referrerTax;

    @Column(name = "referrer_payout_type", length = 64)
    private String referrerPayoutType = "FIXED";

    @Column(name = "txn_splits", columnDefinition = "jsonb")
    private String txnSplits;

    @Column(name = "surcharge", nullable = false, precision = 20, scale = 9)
    private BigDecimal surcharge = BigDecimal.ZERO;

    @Column(name = "surcharge_tax", nullable = false, precision = 20, scale = 9)
    private BigDecimal surchargeTax = BigDecimal.ZERO;

    @Column(name = "emi_bank", length = 64)
    private String emiBank;

    @Column(name = "emi_type", length = 64)
    private String emiType;

    @Column(name = "emi_tenure")
    private Integer emiTenure;

    @Column(name = "txn_basket", columnDefinition = "jsonb")
    private String txnBasket;

    @Column(name = "is_domestic", length = 64)
    private String isDomestic;

    @Column(name = "card_corp_type", length = 64)
    private String cardCorpType;

    @Column(name = "is_nb_corp", length = 64)
    private String isNbCorp;

    @Column(name = "rrn", length = 64)
    private String rrn;

    @Column(name = "order_type", length = 64)
    private String orderType;

    @Column(name = "arn", length = 64)
    private String arn;

    @Column(name = "acquirer_mdr_fees", precision = 24, scale = 9)
    private BigDecimal acquirerMdrFees;

    @Column(name = "acquirer_platform_fees", precision = 24, scale = 9)
    private BigDecimal acquirerPlatformFees;

    @Column(name = "acquirer_others_fees", precision = 24, scale = 9)
    private BigDecimal acquirerOthersFees;

    @Column(name = "acquirer_fee_mode", length = 64)
    private String acquirerFeeMode;

    @Column(name = "acquirer_txn_date")
    private LocalDateTime acquirerTxnDate;

    @Column(name = "acquirer_settled_at")
    private LocalDateTime acquirerSettledAt;

    @Column(name = "acquirer_processed_fees", nullable = false, precision = 24, scale = 9)
    private BigDecimal acquirerProcessedFees = BigDecimal.ZERO;

    @Column(name = "acquirer_processed_tax", nullable = false, precision = 24, scale = 9)
    private BigDecimal acquirerProcessedTax = BigDecimal.ZERO;

    @Column(name = "offer_details", columnDefinition = "jsonb")
    private String offerDetails;

    @Column(name = "multi_acquirer_txn_status", length = 128)
    private String multiAcquirerTxnStatus;

    @Column(name = "multi_txn_count")
    private Integer multiTxnCount;

    @Column(name = "ref_id1", length = 64)
    private String refId1;

    @Column(name = "callisto_udf1", length = 512)
    private String callistoUdf1;

    @Column(name = "callisto_udf2", length = 512)
    private String callistoUdf2;

    @Column(name = "callisto_udf3", length = 512)
    private String callistoUdf3;

    @Column(name = "callisto_udf4", length = 512)
    private String callistoUdf4;

    @Column(name = "callisto_udf5", length = 512)
    private String callistoUdf5;

    @Column(name = "merchant_business_type", length = 64, nullable = false)
    private String merchantBusinessType = "DOMESTIC";

    @Column(name = "exchange_rate", precision = 24, scale = 9)
    private BigDecimal exchangeRate;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSysATxnId() { return sysATxnId; }
    public void setSysATxnId(String sysATxnId) { this.sysATxnId = sysATxnId; }

    public String getSysBTxnId() { return sysBTxnId; }
    public void setSysBTxnId(String sysBTxnId) { this.sysBTxnId = sysBTxnId; }

    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }

    public String getReconId() { return reconId; }
    public void setReconId(String reconId) { this.reconId = reconId; }

    public String getUtrNo() { return utrNo; }
    public void setUtrNo(String utrNo) { this.utrNo = utrNo; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getSubEntityId() { return subEntityId; }
    public void setSubEntityId(String subEntityId) { this.subEntityId = subEntityId; }

    public String getPaymentEntity() { return paymentEntity; }
    public void setPaymentEntity(String paymentEntity) { this.paymentEntity = paymentEntity; }

    public String getPaymentSubEntity() { return paymentSubEntity; }
    public void setPaymentSubEntity(String paymentSubEntity) { this.paymentSubEntity = paymentSubEntity; }

    public String getPmt() { return pmt; }
    public void setPmt(String pmt) { this.pmt = pmt; }

    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }

    public BigDecimal getTxnAmount() { return txnAmount; }
    public void setTxnAmount(BigDecimal txnAmount) { this.txnAmount = txnAmount; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getHoldAmount() { return holdAmount; }
    public void setHoldAmount(BigDecimal holdAmount) { this.holdAmount = holdAmount; }

    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }

    public Integer getTxnCurrency() { return txnCurrency; }
    public void setTxnCurrency(Integer txnCurrency) { this.txnCurrency = txnCurrency; }

    public Integer getSettlementCurrency() { return settlementCurrency; }
    public void setSettlementCurrency(Integer settlementCurrency) { this.settlementCurrency = settlementCurrency; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }

    public String getUdf1() { return udf1; }
    public void setUdf1(String udf1) { this.udf1 = udf1; }

    public String getUdf2() { return udf2; }
    public void setUdf2(String udf2) { this.udf2 = udf2; }

    public String getUdf3() { return udf3; }
    public void setUdf3(String udf3) { this.udf3 = udf3; }

    public String getUdf4() { return udf4; }
    public void setUdf4(String udf4) { this.udf4 = udf4; }

    public String getUdf5() { return udf5; }
    public void setUdf5(String udf5) { this.udf5 = udf5; }

    public BigDecimal getUdf6() { return udf6; }
    public void setUdf6(BigDecimal udf6) { this.udf6 = udf6; }

    public BigDecimal getUdf7() { return udf7; }
    public void setUdf7(BigDecimal udf7) { this.udf7 = udf7; }

    public BigDecimal getUdf8() { return udf8; }
    public void setUdf8(BigDecimal udf8) { this.udf8 = udf8; }

    public String getUdf9() { return udf9; }
    public void setUdf9(String udf9) { this.udf9 = udf9; }

    public String getUdf10() { return udf10; }
    public void setUdf10(String udf10) { this.udf10 = udf10; }

    public String getHoldStatus() { return holdStatus; }
    public void setHoldStatus(String holdStatus) { this.holdStatus = holdStatus; }

    public String getFeeMode() { return feeMode; }
    public void setFeeMode(String feeMode) { this.feeMode = feeMode; }

    public BigDecimal getMdrFees() { return mdrFees; }
    public void setMdrFees(BigDecimal mdrFees) { this.mdrFees = mdrFees; }

    public BigDecimal getPlatformFees() { return platformFees; }
    public void setPlatformFees(BigDecimal platformFees) { this.platformFees = platformFees; }

    public BigDecimal getOthersFees() { return othersFees; }
    public void setOthersFees(BigDecimal othersFees) { this.othersFees = othersFees; }

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public LocalDateTime getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDateTime refundDate) { this.refundDate = refundDate; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getDisputeId() { return disputeId; }
    public void setDisputeId(String disputeId) { this.disputeId = disputeId; }

    public String getDisputeStatus() { return disputeStatus; }
    public void setDisputeStatus(String disputeStatus) { this.disputeStatus = disputeStatus; }

    public String getDisputeType() { return disputeType; }
    public void setDisputeType(String disputeType) { this.disputeType = disputeType; }

    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }

    public String getReferrerId() { return referrerId; }
    public void setReferrerId(String referrerId) { this.referrerId = referrerId; }

    public BigDecimal getReferrerFees() { return referrerFees; }
    public void setReferrerFees(BigDecimal referrerFees) { this.referrerFees = referrerFees; }

    public BigDecimal getReferrerTax() { return referrerTax; }
    public void setReferrerTax(BigDecimal referrerTax) { this.referrerTax = referrerTax; }

    public String getReferrerPayoutType() { return referrerPayoutType; }
    public void setReferrerPayoutType(String referrerPayoutType) { this.referrerPayoutType = referrerPayoutType; }

    public String getTxnSplits() { return txnSplits; }
    public void setTxnSplits(String txnSplits) { this.txnSplits = txnSplits; }

    public BigDecimal getSurcharge() { return surcharge; }
    public void setSurcharge(BigDecimal surcharge) { this.surcharge = surcharge; }

    public BigDecimal getSurchargeTax() { return surchargeTax; }
    public void setSurchargeTax(BigDecimal surchargeTax) { this.surchargeTax = surchargeTax; }

    public String getEmiBank() { return emiBank; }
    public void setEmiBank(String emiBank) { this.emiBank = emiBank; }

    public String getEmiType() { return emiType; }
    public void setEmiType(String emiType) { this.emiType = emiType; }

    public Integer getEmiTenure() { return emiTenure; }
    public void setEmiTenure(Integer emiTenure) { this.emiTenure = emiTenure; }

    public String getTxnBasket() { return txnBasket; }
    public void setTxnBasket(String txnBasket) { this.txnBasket = txnBasket; }

    public String getIsDomestic() { return isDomestic; }
    public void setIsDomestic(String isDomestic) { this.isDomestic = isDomestic; }

    public String getCardCorpType() { return cardCorpType; }
    public void setCardCorpType(String cardCorpType) { this.cardCorpType = cardCorpType; }

    public String getIsNbCorp() { return isNbCorp; }
    public void setIsNbCorp(String isNbCorp) { this.isNbCorp = isNbCorp; }

    public String getRrn() { return rrn; }
    public void setRrn(String rrn) { this.rrn = rrn; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getArn() { return arn; }
    public void setArn(String arn) { this.arn = arn; }

    public BigDecimal getAcquirerMdrFees() { return acquirerMdrFees; }
    public void setAcquirerMdrFees(BigDecimal acquirerMdrFees) { this.acquirerMdrFees = acquirerMdrFees; }

    public BigDecimal getAcquirerPlatformFees() { return acquirerPlatformFees; }
    public void setAcquirerPlatformFees(BigDecimal acquirerPlatformFees) { this.acquirerPlatformFees = acquirerPlatformFees; }

    public BigDecimal getAcquirerOthersFees() { return acquirerOthersFees; }
    public void setAcquirerOthersFees(BigDecimal acquirerOthersFees) { this.acquirerOthersFees = acquirerOthersFees; }

    public String getAcquirerFeeMode() { return acquirerFeeMode; }
    public void setAcquirerFeeMode(String acquirerFeeMode) { this.acquirerFeeMode = acquirerFeeMode; }

    public LocalDateTime getAcquirerTxnDate() { return acquirerTxnDate; }
    public void setAcquirerTxnDate(LocalDateTime acquirerTxnDate) { this.acquirerTxnDate = acquirerTxnDate; }

    public LocalDateTime getAcquirerSettledAt() { return acquirerSettledAt; }
    public void setAcquirerSettledAt(LocalDateTime acquirerSettledAt) { this.acquirerSettledAt = acquirerSettledAt; }

    public BigDecimal getAcquirerProcessedFees() { return acquirerProcessedFees; }
    public void setAcquirerProcessedFees(BigDecimal acquirerProcessedFees) { this.acquirerProcessedFees = acquirerProcessedFees; }

    public BigDecimal getAcquirerProcessedTax() { return acquirerProcessedTax; }
    public void setAcquirerProcessedTax(BigDecimal acquirerProcessedTax) { this.acquirerProcessedTax = acquirerProcessedTax; }

    public String getOfferDetails() { return offerDetails; }
    public void setOfferDetails(String offerDetails) { this.offerDetails = offerDetails; }

    public String getMultiAcquirerTxnStatus() { return multiAcquirerTxnStatus; }
    public void setMultiAcquirerTxnStatus(String multiAcquirerTxnStatus) { this.multiAcquirerTxnStatus = multiAcquirerTxnStatus; }

    public Integer getMultiTxnCount() { return multiTxnCount; }
    public void setMultiTxnCount(Integer multiTxnCount) { this.multiTxnCount = multiTxnCount; }

    public String getRefId1() { return refId1; }
    public void setRefId1(String refId1) { this.refId1 = refId1; }

    public String getCallistoUdf1() { return callistoUdf1; }
    public void setCallistoUdf1(String callistoUdf1) { this.callistoUdf1 = callistoUdf1; }

    public String getCallistoUdf2() { return callistoUdf2; }
    public void setCallistoUdf2(String callistoUdf2) { this.callistoUdf2 = callistoUdf2; }

    public String getCallistoUdf3() { return callistoUdf3; }
    public void setCallistoUdf3(String callistoUdf3) { this.callistoUdf3 = callistoUdf3; }

    public String getCallistoUdf4() { return callistoUdf4; }
    public void setCallistoUdf4(String callistoUdf4) { this.callistoUdf4 = callistoUdf4; }

    public String getCallistoUdf5() { return callistoUdf5; }
    public void setCallistoUdf5(String callistoUdf5) { this.callistoUdf5 = callistoUdf5; }

    public String getMerchantBusinessType() { return merchantBusinessType; }
    public void setMerchantBusinessType(String merchantBusinessType) { this.merchantBusinessType = merchantBusinessType; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
}
