package com.example.findbuilding.services;

import com.example.findbuilding.models.Building;
import com.example.findbuilding.models.User;
import com.example.findbuilding.repositories.BuildingRepository;
import com.example.findbuilding.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BuildingService {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;


    public BuildingService(BuildingRepository buildingRepository, UserRepository userRepository) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
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

    public void deleteBuilding(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(()
            -> new IllegalArgumentException("Пользователь не найден"));

        if (buildingRepository.existsById(id)) {
            removeBuildingFromFavorites(user.getId(), id);
            buildingRepository.deleteById(id);
        }
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

    @Transactional
    public void removeBuildingFromFavorites(Long userId, Long buildingId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не был найден"));
        Building building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new IllegalArgumentException("Здание не найдено"));

        if (user.getFavoriteBuildings().contains(building)) {
            user.getFavoriteBuildings().remove(building);
            userRepository.save(user);
        }
    }



}
