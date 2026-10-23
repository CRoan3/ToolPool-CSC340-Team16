
package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.ReviewCreateRequest;
import com.csc340.toolpool.dto.ReviewResponse;
import com.csc340.toolpool.entity.Rental;
import com.csc340.toolpool.entity.RentalStatus;
import com.csc340.toolpool.entity.Review;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.RentalRepository;
import com.csc340.toolpool.repository.ReviewRepository;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            RentalRepository rentalRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponse createReview(ReviewCreateRequest request) {
        Rental rental = rentalRepository.findById(request.rentalId())
                .orElseThrow(() -> new NotFoundException(
                        "Rental " + request.rentalId() + " was not found."));

        User reviewer = userRepository.findById(request.reviewerId())
                .orElseThrow(() -> new NotFoundException(
                        "User " + request.reviewerId() + " was not found."));

        if (!rental.getRenter().getId().equals(reviewer.getId())) {
            throw new BadRequestException(
                    "Only the renter associated with this rental can leave a review.");
        }

        if (rental.getStatus() != RentalStatus.COMPLETED) {
            throw new BadRequestException(
                    "A review can only be submitted for a completed rental.");
        }

        if (!reviewRepository.findByRentalId(rental.getId()).isEmpty()) {
            throw new ConflictException(
                    "A review has already been submitted for this rental.");
        }

        Review review = new Review();
        review.setRental(rental);
        review.setReviewer(reviewer);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setReviewDate(LocalDate.now());

        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByRental(Long rentalId) {
        if (!rentalRepository.existsById(rentalId)) {
            throw new NotFoundException("Rental " + rentalId + " was not found.");
        }

        return reviewRepository.findByRentalId(rentalId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByReviewer(Long reviewerId) {
        if (!userRepository.existsById(reviewerId)) {
            throw new NotFoundException("User " + reviewerId + " was not found.");
        }

        return reviewRepository.findByReviewerId(reviewerId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }
}
