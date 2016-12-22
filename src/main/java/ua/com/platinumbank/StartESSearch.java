package ua.com.platinumbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = "ua.com.platinumbank")
public class StartESSearch {

    public static void main(String[] args) {

        SpringApplication.run(StartESSearch.class, args);
    }

}