package com.example.FindBuilding.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Building {
    private String name;
    private String address;
    private String workingHours;
    private Float rating;
    private Float coordinateX;
    private Float coordinateZ;
}
