package com.testprosjekt.WeatherDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class WeatherDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherDbApplication.class, args);


	}

	// Lag et script som:
	//Henta data fra en API
	//Behandla dataen
	//Skriv den direkte til en database
	//
	//Eksempel:
	//Hent data fra YR
	//Filtrer, og hent kunn ut f.eks temperatur i Oslo
	//Skriv den til en enkel SQlite database

	//1. Hent data fra Oslo
	//2. Filtrer til nærmaste værstasjon til Gamlebyen
	//3. Prosesser verdi
	//4. Lagre til database med tilhørande metadata
}
