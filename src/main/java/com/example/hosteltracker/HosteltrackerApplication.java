
package com.example.hosteltracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HosteltrackerApplication {

	public static void main(String[] args) {

		SpringApplication.run(
				HosteltrackerApplication.class, args
		);

		System.out.println("\n========== HostelCare ==========");
		System.out.println("Login Page: http://localhost:8081/");
		System.out.println("================================");
	}
}
