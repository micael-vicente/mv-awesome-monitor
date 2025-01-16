package com.mv.ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AwesomeMonitorApp {

	public static void main(String[] args) {
		SpringApplication.run(AwesomeMonitorApp.class, args);
	}

}
