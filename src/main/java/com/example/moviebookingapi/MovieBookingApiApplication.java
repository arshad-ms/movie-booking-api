package com.example.moviebookingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class MovieBookingApiApplication {

	public static void main(String[] args) {

		// to create a test user credentials in db (users table)
		// username: user@test.com
		// password: $2a$10$I/37QRy7K82drn2LP2ZgoerElhG1m9r6adb.Mu4zxqyoQbUbtdQ7K
		// role: USER
		String hash = new BCryptPasswordEncoder().encode("password123");
		System.out.println("BCrypt hash: " + hash);

		SpringApplication.run(MovieBookingApiApplication.class, args);
	}

}
