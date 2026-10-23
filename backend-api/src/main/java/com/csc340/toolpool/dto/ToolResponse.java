
package com.csc340.toolpool.dto;

import com.csc340.toolpool.entity.Tool;
import java.math.BigDecimal;

public record ToolResponse(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal dailyPrice,
        BigDecimal depositAmount,
        Boolean available,
        Long ownerId
) {
    public static ToolResponse from(Tool tool) {
        return new ToolResponse(
                tool.getId(),
                tool.getName(),
                tool.getDescription(),
                tool.getCategory(),
                tool.getDailyPrice(),
                tool.getDepositAmount(),
                tool.getAvailable(),
                tool.getOwner().getId()
        );
    }
}
