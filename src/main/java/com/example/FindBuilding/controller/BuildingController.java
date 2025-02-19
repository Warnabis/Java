package com.example.FindBuilding.controller;

import com.example.FindBuilding.models.Building;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/building")
public class BuildingController {

    @GetMapping
    public Building getBuilding(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String workingHours,
            @RequestParam Float rating,
            @RequestParam Float coordinateX,
            @RequestParam Float coordinateZ) {

        return new Building(name, address, workingHours, rating, coordinateX, coordinateZ);
    }

    @GetMapping("/{id}")
    public Building getBuildingById(@PathVariable String id) {
        return new Building(
                "Building with id " + id,
                "Unknown Address",
                "9:00 - 18:00",
                4.5f,
                50.0f,
                30.0f
        );
    }
}
