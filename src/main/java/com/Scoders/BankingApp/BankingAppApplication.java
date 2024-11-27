package com.Scoders.BankingApp;

import com.Scoders.BankingApp.database.AccountDatabasedummy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingAppApplication.class, args);
	}

	// Use CommandLineRunner to execute the creation and population logic on startup
	@Bean
	public CommandLineRunner run() {
		return args -> {
			// Create account table and insert sample accounts when the app starts
			AccountDatabasedummy.createAccountTable();
		};
	}
}
