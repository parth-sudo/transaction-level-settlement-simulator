package com.juspay.settlement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "settlement")
public class SettlementProperties {

    private Topics topics = new Topics();
    private Batch batch = new Batch();
    private ExternalApi externalApi = new ExternalApi();

    @Data
    public static class Topics {
        private String transactionInstructions = "txn-settlement-instructions";
        private String settlementFlow = "settlement-flow-events";
        private String settlementStatus = "settlement-status-updates";
    }

    @Data
    public static class Batch {
        private int size = 100;
        private long timeoutMs = 5000;
    }

    @Data
    public static class ExternalApi {
        private String baseUrl = "http://localhost:8080";
        private String endpoint = "/api/v1/settle";
        private long timeoutMs = 30000;
    }
}
