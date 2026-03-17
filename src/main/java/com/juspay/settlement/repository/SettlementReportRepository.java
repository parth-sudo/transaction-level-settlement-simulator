package com.juspay.settlement.repository;

import com.juspay.settlement.entity.SettlementReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementReportRepository extends JpaRepository<SettlementReport, Long> {

    List<SettlementReport> findByReconId(String reconId);

    @Query("SELECT sr FROM SettlementReport sr WHERE sr.reconId = :reconId AND sr.status = 'RECONCILED'")
    List<SettlementReport> findReconciledByReconId(@Param("reconId") String reconId);

    @Query("SELECT sr FROM SettlementReport sr WHERE sr.merchantId = :merchantId AND sr.status = 'PENDING_SETTLEMENT'")
    List<SettlementReport> findPendingByMerchantId(@Param("merchantId") String merchantId);

    @Query("SELECT sr.merchantId, sr.acquirerId, COUNT(sr), SUM(sr.amount) " +
           "FROM SettlementReport sr WHERE sr.reconId = :reconId " +
           "GROUP BY sr.merchantId, sr.acquirerId")
    List<Object[]> getSummaryByReconId(@Param("reconId") String reconId);
}
