package ua.com.platinumbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.com.platinumbank")
public class RunTestQueryAll {

	public static void main(String[] args) {
		SpringApplication.run(RunTestQueryAll.class, args);
	}

}
