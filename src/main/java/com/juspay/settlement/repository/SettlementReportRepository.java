package com.juspay.settlement.repository;

import com.juspay.settlement.entity.SettlementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementReportRepository extends JpaRepository<SettlementReport, String> {

    List<SettlementReport> findByReconId(String reconId);

    Optional<SettlementReport> findBySysATxnId(String sysATxnId);

    Optional<SettlementReport> findBySysBTxnId(String sysBTxnId);

    @Query("SELECT sr FROM SettlementReport sr WHERE sr.reconId = :reconId AND sr.utrNo IS NULL")
    List<SettlementReport> findReconciledByReconId(@Param("reconId") String reconId);

    @Query("SELECT sr FROM SettlementReport sr WHERE sr.entityId = :entityId AND sr.utrNo IS NULL")
    List<SettlementReport> findPendingByEntityId(@Param("entityId") String entityId);

    @Query("SELECT sr.entityId, sr.paymentEntity, COUNT(sr), SUM(sr.settlementAmount) " +
           "FROM SettlementReport sr WHERE sr.reconId = :reconId " +
           "GROUP BY sr.entityId, sr.paymentEntity")
    List<Object[]> getSummaryByReconId(@Param("reconId") String reconId);

    @Query("SELECT sr FROM SettlementReport sr WHERE sr.sysATxnId = :txnId OR sr.sysBTxnId = :txnId")
    Optional<SettlementReport> findByTxnId(@Param("txnId") String txnId);
}
