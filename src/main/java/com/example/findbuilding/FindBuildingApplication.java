package com.example.findbuilding;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindBuildingApplication {
    private static final Logger logger = LoggerFactory.getLogger(FindBuildingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FindBuildingApplication.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gracefully...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                logger.error("Shutdown interrupted", e);
                Thread.currentThread().interrupt();
            }
        }));
    }
}
