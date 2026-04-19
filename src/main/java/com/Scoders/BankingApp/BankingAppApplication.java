package com.Scoders.BankingApp;

import com.Scoders.BankingApp.database.AccountDatabasedummy;
import com.Scoders.BankingApp.database.FAQDatabase;
import com.Scoders.BankingApp.database.FAQDataInitializer;
import com.Scoders.BankingApp.database.SupportQuestionDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
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
			// Create tables when the app starts
			UserDatabase.createUserTable();
			AccountDatabasedummy.createAccountTable();
			SupportQuestionDatabase.createSupportQuestionTable();
			FAQDatabase.createFAQTable();
			
			// Initialize sample FAQ data
			FAQDataInitializer.initSampleFAQs();
		};
	}
}
