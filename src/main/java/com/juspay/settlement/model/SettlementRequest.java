package com.juspay.settlement.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {

    @NotBlank(message = "reconId is required")
    private String reconId;

    private String merchantId;
    private String acquirerId;
    private Boolean processAsync;
}
