
package com.csc340.toolpool.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalCreateRequest(
    @NotNull Long toolId,
    @NotNull Long renterId,

    @NotNull
    @FutureOrPresent
    LocalDate startDate,

    @NotNull
    @FutureOrPresent
    LocalDate endDate
) {}
