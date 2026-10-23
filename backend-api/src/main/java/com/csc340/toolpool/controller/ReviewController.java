
package com.csc340.toolpool.controller;

import com.csc340.toolpool.dto.ReviewCreateRequest;
import com.csc340.toolpool.dto.ReviewResponse;
import com.csc340.toolpool.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewResponse review = reviewService.createReview(request);

        return ResponseEntity
                .created(URI.create("/api/reviews/" + review.id()))
                .body(review);
    }

    @GetMapping("/rental/{rentalId}")
    public List<ReviewResponse> getReviewsByRental(
            @PathVariable Long rentalId) {

        return reviewService.getReviewsByRental(rentalId);
    }

    @GetMapping("/reviewer/{reviewerId}")
    public List<ReviewResponse> getReviewsByReviewer(
            @PathVariable Long reviewerId) {

        return reviewService.getReviewsByReviewer(reviewerId);
    }
}

