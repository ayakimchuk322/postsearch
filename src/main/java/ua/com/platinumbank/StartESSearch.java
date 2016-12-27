package ua.com.platinumbank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = "ua.com.platinumbank")
public class StartESSearch {

    // Logger for StartESSearch class
    private static final Logger logger = LogManager.getLogger(StartESSearch.class);

    public static void main(String[] args) {

        try {
            SpringApplication.run(StartESSearch.class, args);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occurred during application startup - {}", e.getMessage());
            }
        }

    }

}
