package com.juspay.settlement.service;

import com.juspay.settlement.entity.SettlementReport;
import com.juspay.settlement.entity.TransactionSettlementInstruction;
import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.model.SettlementRequest;
import com.juspay.settlement.model.SettlementResponse;
import com.juspay.settlement.repository.SettlementReportRepository;
import com.juspay.settlement.repository.TransactionSettlementInstructionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SettlementService {

    private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementReportRepository settlementReportRepository;
    private final TransactionSettlementInstructionRepository instructionRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EventsTrackerService eventsTrackerService;

    public SettlementService(
            SettlementReportRepository settlementReportRepository,
            TransactionSettlementInstructionRepository instructionRepository,
            KafkaProducerService kafkaProducerService,
            EventsTrackerService eventsTrackerService) {
        this.settlementReportRepository = settlementReportRepository;
        this.instructionRepository = instructionRepository;
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

        List<TransactionSettlementInstruction> instructions = new ArrayList<>();
        for (SettlementReport report : reports) {
            TransactionSettlementInstruction instruction = createInstruction(report, request.getReconId());
            instructions.add(instruction);
        }

        List<TransactionSettlementInstruction> savedInstructions = instructionRepository.saveAll(instructions);

        logger.info("Created {} settlement instructions for reconId: {}", savedInstructions.size(), request.getReconId());

        for (TransactionSettlementInstruction instruction : savedInstructions) {
            SettlementEvent event = createInstructionEvent(instruction, batchId);
            kafkaProducerService.publishTxnInstruction(event);
            eventsTrackerService.trackEvent(event, "txn-settlement-instructions");
        }

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
        response.setTotalTransactions(savedInstructions.size());
        response.setProcessedCount(0);
        response.setPendingCount(savedInstructions.size());
        response.setFailedCount(0);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private TransactionSettlementInstruction createInstruction(SettlementReport report, String reconId) {
        TransactionSettlementInstruction instruction = new TransactionSettlementInstruction();
        instruction.setTxnId(report.getTxnId());
        instruction.setReconId(reconId);
        instruction.setMerchantId(report.getMerchantId());
        instruction.setAcquirerId(report.getAcquirerId());
        instruction.setSettlementAmount(report.getAmount());
        instruction.setCurrency(report.getCurrency());
        instruction.setStatus("PENDING");
        instruction.setRetryCount(0);
        return instruction;
    }

    private SettlementEvent createInstructionEvent(TransactionSettlementInstruction instruction, String batchId) {
        SettlementEvent event = new SettlementEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name());
        event.setTxnId(instruction.getTxnId());
        event.setReconId(instruction.getReconId());
        event.setMerchantId(instruction.getMerchantId());
        event.setAcquirerId(instruction.getAcquirerId());
        event.setAmount(instruction.getSettlementAmount());
        event.setCurrency(instruction.getCurrency());
        event.setStatus("PENDING");
        event.setSettlementReference(batchId);
        event.setTimestamp(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    public SettlementResponse getSettlementStatus(String reconId) {
        Long total = instructionRepository.countByReconId(reconId);
        Long settled = instructionRepository.countByReconIdAndStatus(reconId, "SETTLED");
        Long pending = instructionRepository.countByReconIdAndStatus(reconId, "PENDING");
        Long failed = instructionRepository.countByReconIdAndStatus(reconId, "FAILED");

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

    public List<TransactionSettlementInstruction> getPendingInstructions(String reconId) {
        return instructionRepository.findPendingByReconId(reconId);
    }
}
