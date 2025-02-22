package com.example.findbuilding.controller;

import com.example.FindBuilding.models.Building;
import com.example.FindBuilding.services.BuildingService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("checkstyle:MissingJavadocMethod")

@RestController
@RequestMapping("/building")
public class BuildingController {
  private final BuildingService buildingService;

  public BuildingController(BuildingService buildingService) {
    this.buildingService = buildingService;
  }

  @GetMapping
  public List<Building> getBuildings(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String workingHours,
      @RequestParam(required = false) Float rating,
      @RequestParam(required = false) Float coordinateX,
      @RequestParam(required = false) Float coordinateZ) {

    return buildingService.filterBuildings(name, address, workingHours,
      rating, coordinateX, coordinateZ);
  }

  @GetMapping("/{name}")
  public Building getBuildingByName(@PathVariable String name) {
    return buildingService.findByName(name).orElse(null);
  }
}
