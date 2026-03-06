package com.gamified.stocksimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class - Entry point for the Gamified Stock Market Simulator Backend
 * 
 * This class initializes the Spring Boot application and starts the embedded Tomcat server.
 * All components like controllers, services, and repositories are auto-configured by Spring.
 * 
 * To run the application:
 *   mvn spring-boot:run
 * 
 * The application will start on http://localhost:8080
 */
@SpringBootApplication
public class StockSimulatorApplication {
    
    /**
     * Main method - Entry point for the Java application
     * 
     * This method is called when the application starts and it:
     * 1. Initializes the Spring context
     * 2. Scans for annotated components (@Controller, @Service, @Repository, etc.)
     * 3. Starts the embedded web server (Tomcat by default)
     * 
     * @param args Command-line arguments (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(StockSimulatorApplication.class, args);
    }
}
