package com.juspay.settlement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_instructions")
public class SettlementInstruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "settlement_id", length = 50)
    private String settlementId;

    @Column(name = "recon_id", length = 50)
    private String reconId;

    @Column(name = "utr_no", length = 50)
    private String utrNo;

    @Column(name = "entity_id", length = 40)
    private String entityId;

    @Column(name = "sub_entity_id", length = 40)
    private String subEntityId;

    @Column(name = "payment_entity", length = 40)
    private String paymentEntity;

    @Column(name = "payment_sub_entity", length = 40)
    private String paymentSubEntity;

    @Column(name = "pmt", length = 40)
    private String pmt;

    @Column(name = "settlement_status", length = 30, nullable = false)
    private String settlementStatus = "PENDING";

    @Column(name = "settlement_validation_status", length = 30, nullable = false)
    private String settlementValidationStatus = "PENDING";

    @Column(name = "settlement_type", length = 30)
    private String settlementType;

    @Column(name = "order_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal orderAmount = BigDecimal.ZERO;

    @Column(name = "refund_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "chargeback_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal chargebackAmount = BigDecimal.ZERO;

    @Column(name = "dispute_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal disputeAmount = BigDecimal.ZERO;

    @Column(name = "other_charges_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal otherChargesAmount = BigDecimal.ZERO;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "settlement_amount", nullable = false, precision = 15, scale = 4)
    private BigDecimal settlementAmount = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "remarks", length = 1024)
    private String remarks;

    @Column(name = "parent_settlement_id", length = 45)
    private String parentSettlementId;

    @Column(name = "account_id", length = 50)
    private String accountId;

    @Column(name = "settlement_metadata", columnDefinition = "json")
    private String settlementMetadata;

    @Column(name = "udf1", length = 50)
    private String udf1;

    @Column(name = "udf2", length = 50)
    private String udf2;

    @Column(name = "udf3", length = 50)
    private String udf3;

    @Column(name = "udf4", length = 50)
    private String udf4;

    @Column(name = "udf5", length = 50)
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

    @Column(name = "hold_amount", nullable = false, precision = 24, scale = 9)
    private BigDecimal holdAmount = BigDecimal.ZERO;

    @Column(name = "release_amount", nullable = false, precision = 24, scale = 9)
    private BigDecimal releaseAmount = BigDecimal.ZERO;

    @Column(name = "ref_id1", length = 64)
    private String refId1;

    @Column(name = "settlement_category", length = 64)
    private String settlementCategory;

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }

    public String getSettlementValidationStatus() { return settlementValidationStatus; }
    public void setSettlementValidationStatus(String settlementValidationStatus) { this.settlementValidationStatus = settlementValidationStatus; }

    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }

    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public BigDecimal getChargebackAmount() { return chargebackAmount; }
    public void setChargebackAmount(BigDecimal chargebackAmount) { this.chargebackAmount = chargebackAmount; }

    public BigDecimal getDisputeAmount() { return disputeAmount; }
    public void setDisputeAmount(BigDecimal disputeAmount) { this.disputeAmount = disputeAmount; }

    public BigDecimal getOtherChargesAmount() { return otherChargesAmount; }
    public void setOtherChargesAmount(BigDecimal otherChargesAmount) { this.otherChargesAmount = otherChargesAmount; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getParentSettlementId() { return parentSettlementId; }
    public void setParentSettlementId(String parentSettlementId) { this.parentSettlementId = parentSettlementId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getSettlementMetadata() { return settlementMetadata; }
    public void setSettlementMetadata(String settlementMetadata) { this.settlementMetadata = settlementMetadata; }

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

    public BigDecimal getHoldAmount() { return holdAmount; }
    public void setHoldAmount(BigDecimal holdAmount) { this.holdAmount = holdAmount; }

    public BigDecimal getReleaseAmount() { return releaseAmount; }
    public void setReleaseAmount(BigDecimal releaseAmount) { this.releaseAmount = releaseAmount; }

    public String getRefId1() { return refId1; }
    public void setRefId1(String refId1) { this.refId1 = refId1; }

    public String getSettlementCategory() { return settlementCategory; }
    public void setSettlementCategory(String settlementCategory) { this.settlementCategory = settlementCategory; }
}
