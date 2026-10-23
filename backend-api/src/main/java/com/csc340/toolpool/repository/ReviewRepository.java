
package com.csc340.toolpool.repository;

import com.csc340.toolpool.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRentalId(Long rentalId);

    List<Review> findByReviewerId(Long reviewerId);
}

