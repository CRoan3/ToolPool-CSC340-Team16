
package com.csc340.toolpool.dto;

import com.csc340.toolpool.entity.Rental;
import com.csc340.toolpool.entity.RentalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalResponse(
    Long id,
    Long toolId,
    Long renterId,
    LocalDate startDate,
    LocalDate endDate,
    RentalStatus status,
    BigDecimal totalPrice
) {
    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
            rental.getId(),
            rental.getTool().getId(),
            rental.getRenter().getId(),
            rental.getStartDate(),
            rental.getEndDate(),
            rental.getStatus(),
            rental.getTotalPrice()
        );
    }
}

