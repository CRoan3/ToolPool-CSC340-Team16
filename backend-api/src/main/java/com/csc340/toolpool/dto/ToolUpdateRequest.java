
package com.csc340.toolpool.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ToolUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String description,
        @Size(max = 100) String category,
        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal dailyPrice,
        @DecimalMin(value = "0.0") BigDecimal depositAmount
) {}
