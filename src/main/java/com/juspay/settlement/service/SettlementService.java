package com.juspay.settlement.service;

import com.juspay.settlement.entity.SettlementReport;
import com.juspay.settlement.entity.TransactionSettlementInstruction;
import com.juspay.settlement.model.SettlementEvent;
import com.juspay.settlement.model.SettlementRequest;
import com.juspay.settlement.model.SettlementResponse;
import com.juspay.settlement.repository.SettlementReportRepository;
import com.juspay.settlement.repository.TransactionSettlementInstructionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementReportRepository settlementReportRepository;
    private final TransactionSettlementInstructionRepository instructionRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EventsTrackerService eventsTrackerService;

    @Transactional
    public SettlementResponse initiateSettlement(SettlementRequest request) {
        log.info("Initiating settlement for reconId: {}", request.getReconId());

        // Fetch all transactions for this recon batch
        List<SettlementReport> reports = settlementReportRepository.findByReconId(request.getReconId());

        if (reports.isEmpty()) {
            log.warn("No transactions found for reconId: {}", request.getReconId());
            return SettlementResponse.builder()
                    .reconId(request.getReconId())
                    .status("NO_DATA")
                    .message("No transactions found for the given reconId")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // Create batch ID
        String batchId = UUID.randomUUID().toString();

        // Process each transaction
        List<TransactionSettlementInstruction> instructions = reports.stream()
                .map(report -> createInstruction(report, request.getReconId()))
                .collect(Collectors.toList());

        // Save instructions
        List<TransactionSettlementInstruction> savedInstructions = instructionRepository.saveAll(instructions);

        log.info("Created {} settlement instructions for reconId: {}", savedInstructions.size(), request.getReconId());

        // Publish events to Kafka
        for (TransactionSettlementInstruction instruction : savedInstructions) {
            SettlementEvent event = createInstructionEvent(instruction, batchId);
            kafkaProducerService.publishTxnInstruction(event);
            eventsTrackerService.trackEvent(event, "txn-settlement-instructions");
        }

        // Also publish batch start event
        SettlementEvent batchEvent = SettlementEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(SettlementEvent.EventType.BATCH_PROCESSING_STARTED.name())
                .reconId(request.getReconId())
                .settlementReference(batchId)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaProducerService.publishSettlementFlow(batchEvent);
        eventsTrackerService.trackEvent(batchEvent, "settlement-flow-events");

        return SettlementResponse.builder()
                .reconId(request.getReconId())
                .status("INITIATED")
                .message("Settlement batch initiated successfully")
                .batchId(batchId)
                .totalTransactions(savedInstructions.size())
                .processedCount(0)
                .pendingCount(savedInstructions.size())
                .failedCount(0)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private TransactionSettlementInstruction createInstruction(SettlementReport report, String reconId) {
        return TransactionSettlementInstruction.builder()
                .txnId(report.getTxnId())
                .reconId(reconId)
                .merchantId(report.getMerchantId())
                .acquirerId(report.getAcquirerId())
                .settlementAmount(report.getAmount())
                .currency(report.getCurrency())
                .status("PENDING")
                .retryCount(0)
                .build();
    }

    private SettlementEvent createInstructionEvent(TransactionSettlementInstruction instruction, String batchId) {
        return SettlementEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(SettlementEvent.EventType.TXN_INSTRUCTION_CREATED.name())
                .txnId(instruction.getTxnId())
                .reconId(instruction.getReconId())
                .merchantId(instruction.getMerchantId())
                .acquirerId(instruction.getAcquirerId())
                .amount(instruction.getSettlementAmount())
                .currency(instruction.getCurrency())
                .status("PENDING")
                .settlementReference(batchId)
                .timestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SettlementResponse getSettlementStatus(String reconId) {
        Long total = instructionRepository.countByReconId(reconId);
        Long settled = instructionRepository.countByReconIdAndStatus(reconId, "SETTLED");
        Long pending = instructionRepository.countByReconIdAndStatus(reconId, "PENDING");
        Long failed = instructionRepository.countByReconIdAndStatus(reconId, "FAILED");

        String status = pending == 0 && failed == 0 ? "COMPLETED" :
                       failed > 0 ? "PARTIAL_FAILURE" : "IN_PROGRESS";

        return SettlementResponse.builder()
                .reconId(reconId)
                .status(status)
                .totalTransactions(total.intValue())
                .processedCount(settled.intValue())
                .pendingCount(pending.intValue())
                .failedCount(failed.intValue())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public List<TransactionSettlementInstruction> getPendingInstructions(String reconId) {
        return instructionRepository.findPendingByReconId(reconId);
    }
}
