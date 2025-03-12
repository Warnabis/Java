package com.example.findbuilding.controllers;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.Review;
import com.example.findbuilding.models.User;
import com.example.findbuilding.services.BuildingService;
import com.example.findbuilding.services.ReviewService;
import com.example.findbuilding.services.UserService;
import java.util.List;
import java.util.Optional;
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

@Controller
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;
    private final ReviewService reviewService;
    private final UserService userService;

    private static final String REDIRECT_BUILDING = "redirect:/building";

    public BuildingController(BuildingService buildingService,
                              ReviewService reviewService, UserService userService) {
        this.buildingService = buildingService;
        this.reviewService = reviewService;
        this.userService = userService;
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
        Optional<Building> buildingOpt = buildingService.getBuildingById(id);
        if (buildingOpt.isPresent()) {
            Building building = buildingOpt.get();
            building.updateRating();
            model.addAttribute("building", building);
            model.addAttribute("reviews", building.getReviews());
            model.addAttribute("newReview", new Review());
            return "buildingDetails";
        }
        return REDIRECT_BUILDING;
    }

    @PostMapping("/{id}/addReview")
    public String addReview(@PathVariable Long id,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            Model model) {

        Optional<Building> buildingOpt = buildingService.getBuildingById(id);
        if (buildingOpt.isEmpty()) {
            model.addAttribute("error", "Здание не найдено.");
            return REDIRECT_BUILDING;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Пользователь не найден.");
            return "redirect:/login";
        }

        Building building = buildingOpt.get();
        Review newReview = new Review();
        newReview.setRating(rating);
        newReview.setComment(comment);
        newReview.setUser(user);
        newReview.setBuilding(building);

        reviewService.saveReview(newReview);

        building.updateRating();
        buildingService.saveBuilding(building);

        if (!user.getFavoriteBuildings().contains(building)) {
            user.getFavoriteBuildings().add(building);
            userService.saveUser(user);
        }

        model.addAttribute("building", building);
        model.addAttribute("reviews", building.getReviews());
        model.addAttribute("newReview", new Review());

        return "redirect:/building/" + id;
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

    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam Long id, @RequestParam Long reviewId, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        if (currentUser != null) {
            Review review = reviewService.getReviewById(reviewId).orElseThrow(() ->
              new IllegalArgumentException("Отзыв не найден"));
            if (review.getUser().equals(currentUser)) {
                reviewService.deleteReview(reviewId);
            }
        }
        return "redirect:/building" + id;
    }

}
