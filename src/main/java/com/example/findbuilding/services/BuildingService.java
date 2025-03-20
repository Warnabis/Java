package com.example.findbuilding.services;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.User;
import com.example.findbuilding.models.Review;
import com.example.findbuilding.repositories.BuildingRepository;
import com.example.findbuilding.repositories.ReviewRepository;
import com.example.findbuilding.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class BuildingService {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final ReviewRepository reviewRepository;
    private final BuildingCacheService buildingCacheService;

    private static final String REDIRECT_BUILDING = "redirect:/building";

    public BuildingService(BuildingRepository buildingRepository, UserRepository userRepository,
                           ReviewRepository reviewRepository, BuildingCacheService buildingCacheService) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.buildingCacheService = buildingCacheService;
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public List<Building> getAllBuildings(Long id, String name, String address, String workingHours,
                                          Double rating, Double coordinateX, Double coordinateZ) {

        return buildingRepository.findAll().stream()
          .filter(building -> id == null || building.getId().equals(id))
          .filter(building -> name == null || building.getName().equalsIgnoreCase(name))
          .filter(building -> address == null || building.getAddress().equalsIgnoreCase(address))
          .filter(building -> workingHours == null
            || building.getWorkingHours().equalsIgnoreCase(workingHours))
          .filter(building -> rating == null || building.getRating().equals(rating))
          .filter(building -> coordinateX == null || building.getCoordinateX().equals(coordinateX))
          .filter(building -> coordinateZ == null || building.getCoordinateZ().equals(coordinateZ))
          .toList();
    }

    public Optional<Building> getBuildingById(Long id) {
        return buildingRepository.findById(id);
    }

    public void saveBuilding(Building building) {
        buildingRepository.save(building);
    }

    @Transactional
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new IllegalArgumentException("Здание не найдено");
        }

        Building building = buildingRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Здание не найдено"));

        for (User user : building.getFavoritedByUsers()) {
            user.getFavoriteBuildings().remove(building);
        }
        userRepository.saveAll(building.getFavoritedByUsers());
        reviewRepository.deleteAll(building.getReviews());
        buildingRepository.delete(building);
    }

    public List<Building> getNearestBuildings(Long userId) {
        User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return buildingRepository.findAll().stream()
          .sorted((b1, b2) -> {
              double dist1 = getDistance(user.getCoordinateX(),
                user.getCoordinateZ(), b1.getCoordinateX(), b1.getCoordinateZ());
              double dist2 = getDistance(user.getCoordinateX(),
                user.getCoordinateZ(), b2.getCoordinateX(), b2.getCoordinateZ());
              return Double.compare(dist1, dist2);
          })
          .toList();
    }

    private double getDistance(double x1, double z1, double x2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));
    }

    public List<Building> getTopBuildings(boolean useNativeQuery) {
        String cacheKey = "topBuildings:" + useNativeQuery;

        List<Building> cachedResult = buildingCacheService.getCachedQueryResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<Building> topBuildings = useNativeQuery
          ? buildingRepository.findTopBuildingsNative()
          : buildingRepository.findTopBuildings();

        buildingCacheService.cacheQueryResult(cacheKey, topBuildings);
        return topBuildings;
    }

}
