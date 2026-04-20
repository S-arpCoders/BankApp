package com.Scoders.BankingApp;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.AccountDatabasedummy;
import com.Scoders.BankingApp.database.FAQDataInitializer;
import com.Scoders.BankingApp.database.SupportQuestionDatabase;
import com.Scoders.BankingApp.database.TradeDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
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
			AccountDatabase.createAccountTable();
			
			// Create transaction table
			TransactionDatabase.createTransactionTable();
			
			// Create trade table for stock/gold/future trades
			TradeDatabase.createTradeTable();
			
			// Create support question table
			SupportQuestionDatabase.createSupportQuestionTable();
			
			// Initialize FAQ data for smart auto-reply
			FAQDataInitializer.initializeFAQData();
		};
	}
}
