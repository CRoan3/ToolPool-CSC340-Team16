
package com.csc340.toolpool.dto;

import com.csc340.toolpool.entity.Review;
import java.time.LocalDate;

public record ReviewResponse(
    Long id,
    Long rentalId,
    Long reviewerId,
    Integer rating,
    String comment,
    LocalDate reviewDate
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getRental().getId(),
            review.getReviewer().getId(),
            review.getRating(),
            review.getComment(),
            review.getReviewDate()
        );
    }
}

