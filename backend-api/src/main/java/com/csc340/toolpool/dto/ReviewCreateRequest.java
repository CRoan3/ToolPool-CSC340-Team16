
package com.csc340.toolpool.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(
    @NotNull Long rentalId,
    @NotNull Long reviewerId,

    @NotNull
    @Min(1)
    @Max(5)
    Integer rating,

    @Size(max = 2000)
    String comment
) {}

