package com.juspay.settlement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "settlement")
public class SettlementProperties {

    private Topics topics = new Topics();
    private Batch batch = new Batch();
    private ExternalApi externalApi = new ExternalApi();

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public ExternalApi getExternalApi() {
        return externalApi;
    }

    public void setExternalApi(ExternalApi externalApi) {
        this.externalApi = externalApi;
    }

    public static class Topics {
        private String transactionInstructions = "txn-settlement-instructions";
        private String settlementFlow = "settlement-flow-events";
        private String settlementStatus = "settlement-status-updates";

        public String getTransactionInstructions() {
            return transactionInstructions;
        }

        public void setTransactionInstructions(String transactionInstructions) {
            this.transactionInstructions = transactionInstructions;
        }

        public String getSettlementFlow() {
            return settlementFlow;
        }

        public void setSettlementFlow(String settlementFlow) {
            this.settlementFlow = settlementFlow;
        }

        public String getSettlementStatus() {
            return settlementStatus;
        }

        public void setSettlementStatus(String settlementStatus) {
            this.settlementStatus = settlementStatus;
        }
    }

    public static class Batch {
        private int size = 100;
        private long timeoutMs = 5000;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    public static class ExternalApi {
        private String baseUrl = "http://localhost:8080";
        private String endpoint = "/api/v1/settle";
        private long timeoutMs = 30000;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }
}
