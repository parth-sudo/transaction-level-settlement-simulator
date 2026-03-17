package com.juspay.settlement.service;

import com.juspay.settlement.entity.SettlementInstruction;
import com.juspay.settlement.entity.SettlementReport;
import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.model.SettlementRequest;
import com.juspay.settlement.model.SettlementResponse;
import com.juspay.settlement.repository.SettlementInstructionRepository;
import com.juspay.settlement.repository.SettlementReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SettlementService {

    private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementReportRepository settlementReportRepository;
    private final SettlementInstructionRepository settlementInstructionRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EventsTrackerService eventsTrackerService;

    public SettlementService(
            SettlementReportRepository settlementReportRepository,
            SettlementInstructionRepository settlementInstructionRepository,
            KafkaProducerService kafkaProducerService,
            EventsTrackerService eventsTrackerService) {
        this.settlementReportRepository = settlementReportRepository;
        this.settlementInstructionRepository = settlementInstructionRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.eventsTrackerService = eventsTrackerService;
    }

    @Transactional
    public SettlementResponse initiateSettlement(SettlementRequest request) {
        logger.info("Initiating settlement for reconId: {}", request.getReconId());

        List<SettlementReport> reports = settlementReportRepository.findByReconId(request.getReconId());

        if (reports.isEmpty()) {
            logger.warn("No transactions found for reconId: {}", request.getReconId());
            SettlementResponse response = new SettlementResponse();
            response.setReconId(request.getReconId());
            response.setStatus("NO_DATA");
            response.setMessage("No transactions found for the given reconId");
            response.setTimestamp(LocalDateTime.now());
            return response;
        }

        String batchId = UUID.randomUUID().toString();
        int successCount = 0;

        for (SettlementReport report : reports) {
            try {
                SettlementInstruction instruction = createInstruction(report);
                SettlementInstruction savedInstruction = settlementInstructionRepository.save(instruction);

                SettlementEvent event = createInstructionEvent(savedInstruction, batchId);
                kafkaProducerService.publishTxnInstruction(event);
                eventsTrackerService.trackEvent(event, "txn-settlement-instructions");

                successCount++;
            } catch (Exception e) {
                logger.error("Error creating settlement instruction for txn: {}", report.getSysATxnId(), e);
            }
        }

        logger.info("Created {} settlement instructions for reconId: {}", successCount, request.getReconId());

        SettlementEvent batchEvent = new SettlementEvent();
        batchEvent.setEventId(UUID.randomUUID().toString());
        batchEvent.setEventType(SettlementEvent.EventType.BATCH_PROCESSING_STARTED.name());
        batchEvent.setReconId(request.getReconId());
        batchEvent.setSettlementReference(batchId);
        batchEvent.setTimestamp(LocalDateTime.now());
        kafkaProducerService.publishSettlementFlow(batchEvent);
        eventsTrackerService.trackEvent(batchEvent, "settlement-flow-events");

        SettlementResponse response = new SettlementResponse();
        response.setReconId(request.getReconId());
        response.setStatus("INITIATED");
        response.setMessage("Settlement batch initiated successfully");
        response.setBatchId(batchId);
        response.setTotalTransactions(reports.size());
        response.setProcessedCount(0);
        response.setPendingCount(successCount);
        response.setFailedCount(reports.size() - successCount);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    @Transactional
    public SettlementResponse processSingleTransaction(String txnId) {
        logger.info("Processing single transaction for txn_id: {}", txnId);

        // Find the transaction in settlement_report table
        Optional<SettlementReport> reportOpt = settlementReportRepository.findByTxnId(txnId);

        if (reportOpt.isEmpty()) {
            logger.warn("Transaction not found in settlement_report: {}", txnId);
            SettlementResponse response = new SettlementResponse();
            response.setStatus("NOT_FOUND");
            response.setMessage("Transaction not found in settlement report");
            response.setTimestamp(LocalDateTime.now());
            return response;
        }

        SettlementReport report = reportOpt.get();

        // Check if settlement instruction already exists
        Optional<SettlementInstruction> existingInstruction = settlementInstructionRepository
                .findBySettlementId(report.getSettlementId());

        if (existingInstruction.isPresent()) {
            logger.info("Settlement instruction already exists for settlement_id: {}", report.getSettlementId());
            SettlementResponse response = new SettlementResponse();
            response.setReconId(report.getReconId());
            response.setStatus("ALREADY_EXISTS");
            response.setMessage("Settlement instruction already exists with status: " + existingInstruction.get().getSettlementStatus());
            response.setTimestamp(LocalDateTime.now());
            return response;
        }

        // Create settlement instruction
        SettlementInstruction instruction = createInstruction(report);
        SettlementInstruction savedInstruction = settlementInstructionRepository.save(instruction);

        logger.info("Created settlement instruction with id: {} for txn_id: {}", savedInstruction.getId(), txnId);

        // Publish to Kafka
        String eventId = UUID.randomUUID().toString();
        SettlementEvent event = new SettlementEvent();
        event.setEventId(eventId);
        event.setEventType(SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name());
        event.setTxnId(txnId);
        event.setReconId(report.getReconId());
        event.setMerchantId(report.getEntityId());
        event.setAcquirerId(report.getPaymentEntity());
        event.setAmount(report.getSettlementAmount());
        event.setCurrency(report.getTxnCurrency() != null ? report.getTxnCurrency().toString() : "365");
        event.setStatus("PENDING");
        event.setSettlementReference(savedInstruction.getSettlementId());
        event.setTimestamp(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());

        kafkaProducerService.publishTxnInstruction(event);
        eventsTrackerService.trackEvent(event, "txn-settlement-instructions");

        SettlementResponse response = new SettlementResponse();
        response.setReconId(report.getReconId());
        response.setStatus("SUCCESS");
        response.setMessage("Single transaction settlement instruction created and queued");
        response.setTxnId(txnId);
        response.setSettlementId(savedInstruction.getSettlementId());
        response.setTotalTransactions(1);
        response.setProcessedCount(0);
        response.setPendingCount(1);
        response.setFailedCount(0);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private SettlementInstruction createInstruction(SettlementReport report) {
        SettlementInstruction instruction = new SettlementInstruction();
        instruction.setSettlementId(report.getSettlementId() != null ? report.getSettlementId() : UUID.randomUUID().toString());
        instruction.setUtrNo(report.getUtrNo());
        instruction.setEntityId(report.getEntityId());
        instruction.setSubEntityId(report.getSubEntityId());
        instruction.setPaymentEntity(report.getPaymentEntity());
        instruction.setPaymentSubEntity(report.getPaymentSubEntity());
        instruction.setPmt(report.getPmt());
        instruction.setSettlementStatus("PENDING");
        instruction.setSettlementValidationStatus("PENDING");
        instruction.setSettlementType(report.getSettlementType());

        // Map amounts based on transaction type
        if ("REFUND".equalsIgnoreCase(report.getTxnType())) {
            instruction.setRefundAmount(report.getSettlementAmount());
        } else if ("CHARGEBACK".equalsIgnoreCase(report.getTxnType())) {
            instruction.setChargebackAmount(report.getSettlementAmount());
        } else if ("DISPUTE".equalsIgnoreCase(report.getTxnType())) {
            instruction.setDisputeAmount(report.getSettlementAmount());
        } else {
            instruction.setOrderAmount(report.getSettlementAmount());
        }

        instruction.setFeeAmount(report.getFeeAmount() != null ? report.getFeeAmount() : BigDecimal.ZERO);
        instruction.setTaxAmount(report.getTaxAmount() != null ? report.getTaxAmount() : BigDecimal.ZERO);
        instruction.setSettlementAmount(report.getSettlementAmount());
        instruction.setHoldAmount(report.getHoldAmount() != null ? report.getHoldAmount() : BigDecimal.ZERO);
        instruction.setReleaseAmount(BigDecimal.ZERO);

        return instruction;
    }

    private SettlementEvent createInstructionEvent(SettlementInstruction instruction, String batchId) {
        SettlementEvent event = new SettlementEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name());
        event.setMerchantId(instruction.getEntityId());
        event.setAcquirerId(instruction.getPaymentEntity());
        event.setAmount(instruction.getSettlementAmount());
        event.setCurrency("365");
        event.setStatus("PENDING");
        event.setSettlementReference(batchId);
        event.setTimestamp(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    public SettlementResponse getSettlementStatus(String reconId) {
        // Get summary from settlement_instructions table
        List<Object[]> summary = settlementReportRepository.getSummaryByReconId(reconId);

        Long total = settlementInstructionRepository.countByReconId(reconId);
        Long settled = settlementInstructionRepository.countByReconIdAndStatus(reconId, "SETTLED");
        Long pending = settlementInstructionRepository.countByReconIdAndStatus(reconId, "PENDING");
        Long failed = settlementInstructionRepository.countByReconIdAndStatus(reconId, "FAILED");

        String status = (pending == 0 && failed == 0) ? "COMPLETED" :
                       (failed > 0) ? "PARTIAL_FAILURE" : "IN_PROGRESS";

        SettlementResponse response = new SettlementResponse();
        response.setReconId(reconId);
        response.setStatus(status);
        response.setTotalTransactions(total.intValue());
        response.setProcessedCount(settled.intValue());
        response.setPendingCount(pending.intValue());
        response.setFailedCount(failed.intValue());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public List<SettlementInstruction> getPendingInstructions(String reconId) {
        return settlementInstructionRepository.findPendingByReconId(reconId);
    }
}
