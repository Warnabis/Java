package com.example.findbuilding.services;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.Review;
import com.example.findbuilding.models.User;
import com.example.findbuilding.repositories.ReviewRepository;
import java.util.List;
import java.util.Optional;

import com.example.findbuilding.repositories.UserRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BuildingCacheService buildingCacheService;
    private final BuildingService buildingService;
    private final UserService userService;
    private static final String REDIRECT_BUILDING = "redirect:/building";

    public ReviewService(ReviewRepository reviewRepository, BuildingService buildingService, UserService userService,
                         BuildingCacheService buildingCacheService) {
        this.reviewRepository = reviewRepository;
        this.buildingService = buildingService;
        this.userService = userService;
        this.buildingCacheService = buildingCacheService;
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

    public String addReviewToBuilding(Long buildingId, int rating, String comment, Model model) {
        Optional<Building> buildingOpt = buildingService.getBuildingById(buildingId);
        if (buildingOpt.isEmpty()) {
            model.addAttribute("error", "Здание не найдено.");
            return REDIRECT_BUILDING;
        }

        Building building = buildingOpt.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Пользователь не найден.");
            return "redirect:/login";
        }

        Review newReview = new Review();
        newReview.setRating(rating);
        newReview.setComment(comment);
        newReview.setUser(user);
        newReview.setBuilding(building);

        reviewRepository.save(newReview);

        building.updateRating();
        buildingService.saveBuilding(building);

        if (!user.getFavoriteBuildings().contains(building)) {
            user.getFavoriteBuildings().add(building);
            userService.saveUser(user);
        }

        Optional<Building> updatedBuildingOpt = buildingService.getBuildingById(buildingId);
        if (updatedBuildingOpt.isPresent()) {
            building = updatedBuildingOpt.get();
        }

        model.addAttribute("building", building);
        model.addAttribute("reviews", building.getReviews());
        model.addAttribute("newReview", new Review());

        buildingCacheService.updateBuildingInCache(buildingId, building);

        return "redirect:/building/" + buildingId;
    }


}
