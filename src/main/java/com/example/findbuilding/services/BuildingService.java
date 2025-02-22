package com.example.findbuilding.services;

import com.example.findbuilding.models.Building;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@SuppressWarnings("checkstyle:MissingJavadocMethod")

@Service
public class BuildingService {
  private final List<Building> buildings = new ArrayList<>();

  public BuildingService() {
    buildings.add(new Building("A", "Address 1",
        "08:00 - 22:00", 4.5f, 10f, 20f));
    buildings.add(new Building("B", "Address 2",
        "09:00 - 20:00", 4.2f, 30f, 40f));
    buildings.add(new Building("A", "Address 3",
        "10:00 - 21:00", 4.8f, 50f, 60f));
  }

  public List<Building> list() {
    return buildings;
  }

  public List<Building> filterBuildings(String name, String address, String workingHours,
                                        Float rating, Float coordinateX, Float coordinateZ) {
    return buildings.stream()
      .filter(b -> name == null || b.getName().equalsIgnoreCase(name))
      .filter(b -> address == null || b.getAddress().equalsIgnoreCase(address))
      .filter(b -> workingHours == null || b.getWorkingHours().equalsIgnoreCase(workingHours))
      .filter(b -> rating == null || b.getRating().equals(rating))
      .filter(b -> coordinateX == null || b.getCoordinateX().equals(coordinateX))
      .filter(b -> coordinateZ == null || b.getCoordinateZ().equals(coordinateZ))
      .toList();
  }

  public Optional<Building> findByName(String name) {
    return buildings.stream().filter(b -> b.getName().equalsIgnoreCase(name)).findFirst();
  }
}
