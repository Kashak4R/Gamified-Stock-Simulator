package com.gamified.stocksimulator.controller;

import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * UserController Class - REST API Endpoints for User Management
 * 
 * This controller handles HTTP requests related to user operations.
 * It receives requests from the frontend and delegates to UserService for processing.
 * 
 * Key Concepts:
 *   - @RestController - Marks this class as a REST API controller
 *   - @RequestMapping - Sets the base URL path for all endpoints in this controller
 *   - @PostMapping/@GetMapping - Defines HTTP method and endpoint path
 *   - @RequestBody - Receives data from request body (usually JSON)
 *   - @PathVariable - Extracts values from URL path (e.g., /users/{id})
 *   - ResponseEntity - Allows customization of HTTP response (status codes, headers, body)
 * 
 * URL Pattern:
 *   - Base URL: http://localhost:8080/api/users
 *   - Register: POST /api/users/register
 *   - Login: POST /api/users/login
 *   - Get User: GET /api/users/{userId}
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    /**
     * UserService - Injected by Spring to handle business logic
     */
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user - POST /api/users/register
     * 
     * This endpoint allows users to create a new account.
     * Receives registration data and creates a new user in the system.
     * 
     * Request Body Example:
     * {
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "password": "MySecurePassword123",
     *   "initialBalance": 10000.00
     * }
     * 
     * Response: The newly created user object with ID
     * 
     * @param requestBody Map containing username, email, password, initialBalance
     * @return ResponseEntity with created user or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract data from request body
            String username = (String) requestBody.get("username");
            String email = (String) requestBody.get("email");
            String password = (String) requestBody.get("password");
            BigDecimal initialBalance = new BigDecimal(requestBody.get("initialBalance").toString());
            
            // Validate inputs - basic checks
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Initial balance must be greater than 0"));
            }
            
            // Call service to register the user
            User newUser = userService.registerUser(username, email, password, initialBalance);
            
            // Return success response with user data
            // Note: Password should never be returned to client
            return ResponseEntity.ok(Map.of(
                "userId", newUser.getUserId(),
                "username", newUser.getUsername(),
                "email", newUser.getEmail(),
                "balance", newUser.getBalance(),
                "message", "User registered successfully"
            ));
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors (duplicate username/email, etc.)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.internalServerError().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get user by ID - GET /api/users/{userId}
     * 
     * This endpoint retrieves a specific user's information by their ID.
     * 
     * URL Example: GET /api/users/1
     * 
     * Response: The user object
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with user data or error message
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            // Call service to find user by ID
            Optional<User> user = userService.getUserById(userId);
            
            // Check if user exists
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Return user data
            User foundUser = user.get();
            return ResponseEntity.ok(Map.of(
                "userId", foundUser.getUserId(),
                "username", foundUser.getUsername(),
                "email", foundUser.getEmail(),
                "balance", foundUser.getBalance(),
                "initialBalance", foundUser.getInitialBalance(),
                "isActive", foundUser.getIsActive()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve user"));
        }
    }
    
    /**
     * Get user by username - GET /api/users/username/{username}
     * 
     * This endpoint finds a user by their username.
     * 
     * URL Example: GET /api/users/username/johndoe
     * 
     * Response: The user object if found
     * 
     * @param username The username to search for
     * @return ResponseEntity with user data or error message
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            // Call service to find user by username
            Optional<User> user = userService.getUserByUsername(username);
            
            // Check if user exists
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Return user data
            User foundUser = user.get();
            return ResponseEntity.ok(Map.of(
                "userId", foundUser.getUserId(),
                "username", foundUser.getUsername(),
                "email", foundUser.getEmail(),
                "balance", foundUser.getBalance()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve user"));
        }
    }
    
    /**
     * Get user's current balance - GET /api/users/{userId}/balance
     * 
     * This endpoint retrieves the current cash balance of a user.
     * This is the money available for trading.
     * 
     * URL Example: GET /api/users/1/balance
     * 
     * Response: The user's current balance
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with balance amount or error message
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getUserBalance(@PathVariable Long userId) {
        try {
            // Call service to find user
            Optional<User> user = userService.getUserById(userId);
            
            // Check if user exists
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Return balance
            User foundUser = user.get();
            return ResponseEntity.ok(Map.of(
                "userId", foundUser.getUserId(),
                "balance", foundUser.getBalance(),
                "initialBalance", foundUser.getInitialBalance()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve balance"));
        }
    }
    
    /**
     * Calculate user's profit/loss - GET /api/users/{userId}/profitloss
     * 
     * This endpoint calculates how much profit or loss the user has made.
     * Formula: Current balance + portfolio value - initial balance
     * 
     * URL Example: GET /api/users/1/profitloss
     * 
     * Response: Profit or loss amount and percentage
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with profit/loss calculation or error message
     */
    @GetMapping("/{userId}/profitloss")
    public ResponseEntity<?> getUserProfitLoss(@PathVariable Long userId) {
        try {
            // Call service to find user
            Optional<User> user = userService.getUserById(userId);
            
            // Check if user exists
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Calculate profit/loss
            User foundUser = user.get();
            BigDecimal profitLoss = userService.calculateProfitLoss(foundUser);
            BigDecimal initialBalance = foundUser.getInitialBalance();
            
            // Calculate percentage change
            BigDecimal percentChange = profitLoss.divide(initialBalance, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            
            // Return result
            return ResponseEntity.ok(Map.of(
                "userId", foundUser.getUserId(),
                "profitLoss", profitLoss,
                "percentChange", percentChange + "%",
                "initialBalance", initialBalance,
                "currentBalance", foundUser.getBalance()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to calculate profit/loss"));
        }
    }
}
