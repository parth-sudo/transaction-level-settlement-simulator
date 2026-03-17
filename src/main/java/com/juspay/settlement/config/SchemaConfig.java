package com.juspay.settlement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "schema")
public class SchemaConfig {

    private String reconSchema = "recon";
    private String settlementSchema = "settlement_20230727";

    public String getReconSchema() {
        return reconSchema;
    }

    public void setReconSchema(String reconSchema) {
        this.reconSchema = reconSchema;
    }

    public String getSettlementSchema() {
        return settlementSchema;
    }

    public void setSettlementSchema(String settlementSchema) {
        this.settlementSchema = settlementSchema;
    }
}
