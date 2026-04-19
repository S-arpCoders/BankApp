package com.Scoders.BankingApp;

import com.Scoders.BankingApp.database.AccountDatabasedummy;
import com.Scoders.BankingApp.database.FAQDataInitializer;
import com.Scoders.BankingApp.database.SupportQuestionDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
			
			// Create support question table
			SupportQuestionDatabase.createSupportQuestionTable();
			
			// Initialize FAQ data for smart auto-reply
			FAQDataInitializer.initializeFAQData();
		};
	}
}
