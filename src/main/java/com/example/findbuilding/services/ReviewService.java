package com.example.findbuilding.services;

import com.example.findbuilding.models.Review;
import com.example.findbuilding.repositories.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public Review saveReview(Review review) {
        try {
            return reviewRepository.save(review);
        } catch (ObjectOptimisticLockingFailureException e) {
            if (review.getId() == null) {
                throw new
                  IllegalStateException("Cannot handle optimistic locking for a new review", e);
            }
            Review freshReview = reviewRepository.findById(review.getId()).orElseThrow();
            freshReview.setComment(review.getComment());
            freshReview.setRating(review.getRating());
            return reviewRepository.save(freshReview);
        }
    }

}
