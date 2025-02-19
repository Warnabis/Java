package com.example.FindBuilding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class FindBuildingApplication {
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Shutting down gracefully...");
			try {
				TimeUnit.SECONDS.sleep(2); // Подожди перед закрытием
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}));
		SpringApplication.run(FindBuildingApplication.class, args);
	}
}
