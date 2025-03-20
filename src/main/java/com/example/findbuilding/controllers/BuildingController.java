package com.example.findbuilding.controllers;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.Review;
import com.example.findbuilding.models.User;
import com.example.findbuilding.services.BuildingCacheService;
import com.example.findbuilding.services.BuildingService;
import com.example.findbuilding.services.ReviewService;
import com.example.findbuilding.services.UserService;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;
    private final ReviewService reviewService;
    private final UserService userService;
    public final BuildingCacheService buildingCacheService;

    private static final String REDIRECT_BUILDING = "redirect:/building";

    public BuildingController(BuildingService buildingService,
                              ReviewService reviewService, UserService userService, BuildingCacheService buildingCacheService ) {
        this.buildingService = buildingService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.buildingCacheService = buildingCacheService;
    }

    @GetMapping
    public String showBuildingPage(@RequestParam(required = false) Long id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String address,
                                   @RequestParam(required = false) String workingHours,
                                   @RequestParam(required = false) Double rating,
                                   @RequestParam(required = false) Double coordinateX,
                                   @RequestParam(required = false) Double coordinateZ,
                                   Model model) {
        List<Building> buildings = buildingService.getAllBuildings(id, name,
            address, workingHours, rating, coordinateX, coordinateZ);
        model.addAttribute("buildings", buildings);
        return "buildings";
    }

    @GetMapping("/{id}")
    public String getBuildingById(@PathVariable Long id, Model model) {
        long startTime = System.nanoTime(); // Засекаем время начала
        log.info("Запрос информации о здании ID: {}", id);

        Building building = buildingCacheService.getCachedBuilding(id);
        if (building == null) {
            log.info("Здание ID: {} не найдено в кэше, загружаем из БД", id);
            Optional<Building> buildingOpt = buildingService.getBuildingById(id);
            if (buildingOpt.isEmpty()) {
                log.warn("Здание ID: {} не найдено в базе данных", id);
                return REDIRECT_BUILDING;
            }
            building = buildingOpt.get();
            buildingCacheService.cacheBuilding(id, building);
            log.info("Здание ID: {} добавлено в кэш", id);
        } else {
            log.info("Здание ID: {} загружено из кэша", id);
        }

        long duration = (System.nanoTime() - startTime) / 1_000_000;
        log.info("Время открытия здания ID: {} составило {} мс", id, duration);

        building.updateRating();
        model.addAttribute("building", building);
        model.addAttribute("reviews", building.getReviews());
        model.addAttribute("newReview", new Review());

        return "buildingDetails";
    }

    @PostMapping("/{id}/addReview")
    public String addReview(@PathVariable Long id,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            Model model) {
        return reviewService.addReviewToBuilding(id, rating, comment, model);
    }

    @PostMapping("/create")
    public String createBuilding(@ModelAttribute Building building) {
        buildingService.saveBuilding(building);
        return REDIRECT_BUILDING;
    }

    @PostMapping("/delete/{id}")
    public String deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return REDIRECT_BUILDING;
    }

    @PostMapping("/update/{id}")
    public String updateBuilding(@PathVariable Long id, @ModelAttribute Building updatedBuilding) {
        Optional<Building> existingBuilding = buildingService.getBuildingById(id);
        if (existingBuilding.isPresent()) {
            Building building = existingBuilding.get();
            building.setName(updatedBuilding.getName());
            building.setAddress(updatedBuilding.getAddress());
            building.setWorkingHours(updatedBuilding.getWorkingHours());
            building.setRating(updatedBuilding.getRating());
            building.setCoordinateX(updatedBuilding.getCoordinateX());
            building.setCoordinateZ(updatedBuilding.getCoordinateZ());
            buildingService.saveBuilding(building);
        }
        return REDIRECT_BUILDING;
    }

    @PostMapping("/{id}/deleteReview")
    public String deleteReview(@PathVariable Long id,
                               @RequestParam Long reviewId, Authentication authentication) {

        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        if (currentUser != null) {
            Review review = reviewService.getReviewById(reviewId).orElseThrow(()
              -> new IllegalArgumentException("Отзыв не найден"));
            if (review.getUser().equals(currentUser)) {
                reviewService.deleteReview(reviewId);
            }
        }

        return "redirect:/building/" + id;
    }

}
