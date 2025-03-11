package com.example.findbuilding.utilits;

import java.util.Random;

public class CoordinateGenerator {

    private static final Random random = new Random();

    public static Double generateCoordinateX() {
        return -180 + (180 - (-180)) * random.nextDouble();
    }

    public static Double generateCoordinateZ() {
        return -90 + (90 - (-90)) * random.nextDouble();
    }
}
