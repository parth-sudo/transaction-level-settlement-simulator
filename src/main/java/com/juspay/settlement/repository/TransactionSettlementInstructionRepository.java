package com.juspay.settlement.repository;

import com.juspay.settlement.entity.TransactionSettlementInstruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionSettlementInstructionRepository extends JpaRepository<TransactionSettlementInstruction, Long> {

    List<TransactionSettlementInstruction> findByReconId(String reconId);

    List<TransactionSettlementInstruction> findByMerchantIdAndStatus(String merchantId, String status);

    List<TransactionSettlementInstruction> findByStatus(String status);

    Optional<TransactionSettlementInstruction> findByTxnId(String txnId);

    @Query("SELECT tsi FROM TransactionSettlementInstruction tsi " +
           "WHERE tsi.reconId = :reconId AND tsi.status = 'PENDING'")
    List<TransactionSettlementInstruction> findPendingByReconId(@Param("reconId") String reconId);

    @Modifying
    @Query("UPDATE TransactionSettlementInstruction tsi SET tsi.status = :status, " +
           "tsi.settlementReference = :reference, tsi.settledAt = CURRENT_TIMESTAMP " +
           "WHERE tsi.txnId = :txnId")
    int updateStatusByTxnId(@Param("txnId") String txnId,
                            @Param("status") String status,
                            @Param("reference") String reference);

    @Modifying
    @Query("UPDATE TransactionSettlementInstruction tsi SET tsi.status = :status, " +
           "tsi.retryCount = tsi.retryCount + 1, tsi.errorMessage = :errorMessage " +
           "WHERE tsi.txnId = :txnId")
    int updateFailureStatus(@Param("txnId") String txnId,
                            @Param("status") String status,
                            @Param("errorMessage") String errorMessage);

    @Query("SELECT COUNT(tsi) FROM TransactionSettlementInstruction tsi WHERE tsi.reconId = :reconId")
    Long countByReconId(@Param("reconId") String reconId);

    @Query("SELECT COUNT(tsi) FROM TransactionSettlementInstruction tsi " +
           "WHERE tsi.reconId = :reconId AND tsi.status = :status")
    Long countByReconIdAndStatus(@Param("reconId") String reconId, @Param("status") String status);
}
