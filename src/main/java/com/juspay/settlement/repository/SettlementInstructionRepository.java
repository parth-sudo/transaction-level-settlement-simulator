package com.juspay.settlement.repository;

import com.juspay.settlement.entity.SettlementInstruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementInstructionRepository extends JpaRepository<SettlementInstruction, String> {

    Optional<SettlementInstruction> findBySettlementId(String settlementId);

    List<SettlementInstruction> findByEntityId(String entityId);

    List<SettlementInstruction> findBySettlementStatus(String settlementStatus);

    List<SettlementInstruction> findByEntityIdAndSettlementStatus(String entityId, String settlementStatus);

    @Query("SELECT si FROM SettlementInstruction si WHERE si.settlementStatus = 'PENDING'")
    List<SettlementInstruction> findPendingInstructions();

    @Query("SELECT si FROM SettlementInstruction si WHERE si.entityId = :entityId AND si.settlementStatus = 'PENDING'")
    List<SettlementInstruction> findPendingByEntityId(@Param("entityId") String entityId);

    @Modifying
    @Query("UPDATE SettlementInstruction si SET si.settlementStatus = :status, si.utrNo = :utrNo, si.updatedAt = CURRENT_TIMESTAMP WHERE si.settlementId = :settlementId")
    int updateStatusBySettlementId(@Param("settlementId") String settlementId,
                                   @Param("status") String status,
                                   @Param("utrNo") String utrNo);

    @Modifying
    @Query("UPDATE SettlementInstruction si SET si.settlementStatus = :status, si.remarks = :remarks, si.updatedAt = CURRENT_TIMESTAMP WHERE si.settlementId = :settlementId")
    int updateFailureStatus(@Param("settlementId") String settlementId,
                            @Param("status") String status,
                            @Param("remarks") String remarks);

    @Query("SELECT COUNT(si) FROM SettlementInstruction si WHERE si.entityId = :entityId")
    Long countByEntityId(@Param("entityId") String entityId);

    @Query("SELECT COUNT(si) FROM SettlementInstruction si WHERE si.entityId = :entityId AND si.settlementStatus = :status")
    Long countByEntityIdAndStatus(@Param("entityId") String entityId, @Param("status") String status);

    List<SettlementInstruction> findByParentSettlementId(String parentSettlementId);

    @Modifying
    @Query("UPDATE SettlementInstruction si SET si.settlementStatus = :status, si.remarks = :remarks, si.updatedAt = CURRENT_TIMESTAMP WHERE si.txnIdentifier = :txnIdentifier")
    int updateStatusByTxnIdentifier(@Param("txnIdentifier") String txnIdentifier,
                                     @Param("status") String status,
                                     @Param("remarks") String remarks);

    @Query("SELECT si.entityId, SUM(si.settlementAmount), SUM(si.feeAmount), SUM(si.taxAmount), si.settlementType " +
           "FROM SettlementInstruction si WHERE si.entityId = :entityId GROUP BY si.entityId, si.settlementType")
    List<Object[]> getSettlementReportByEntityId(@Param("entityId") String entityId);
}
