package com.gamified.stocksimulator.service;

import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UserService Class - Business Logic Layer for User Management
 * 
 * This service contains all business logic related to user operations.
 * It acts as an intermediary between the Controller (API) and Repository (Database).
 * 
 * Key Responsibilities:
 *   - User registration and authentication
 *   - User profile management
 *   - Balance updates and account management
 *   - Validation of user inputs
 * 
 * Annotation Explanations:
 *   @Service - Marks this class as a service component (business logic layer)
 *   @Transactional - Ensures database operations are atomic (all or nothing)
 *   @Autowired - Automatically injects dependencies (UserRepository, PasswordEncoder)
 */
@Service
@Transactional
public class UserService {
    
    /**
     * UserRepository - Used to perform database operations on User entity
     * Automatically injected by Spring
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * PasswordEncoder - Used to encrypt passwords for security
     * Automatically injected by Spring Security
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user in the system
     * 
     * This method creates a new user account with the following steps:
     * 1. Check if username already exists - throw error if it does
     * 2. Check if email already exists - throw error if it does
     * 3. Encrypt the password using bcrypt for security
     * 4. Create a new User object with initial balance
     * 5. Save the user to the database
     * 
     * @param username The desired username (must be unique)
     * @param email The email address (must be unique)
     * @param password The plain text password (will be encrypted)
     * @param initialBalance The starting amount of money for trading
     * @return The newly created User object
     * @throws IllegalArgumentException if username or email already exists
     */
    public User registerUser(String username, String email, String password, BigDecimal initialBalance) {
        // Check if username already exists in database
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        // Check if email already exists in database
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        // Create a new User object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        
        // Encrypt password before saving - NEVER save plain text passwords!
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        
        // Set initial balance and current balance to the same amount
        user.setBalance(initialBalance);
        user.setInitialBalance(initialBalance);
        
        // Set account as active
        user.setIsActive(true);
        
        // Save the new user to the database
        // The @PrePersist method in User entity will set timestamps automatically
        return userRepository.save(user);
    }
    
    /**
     * Find a user by their username
     * 
     * Retrieves a user from the database using their username.
     * Returns an Optional which may or may not contain a user.
     * 
     * @param username The username to search for
     * @return Optional<User> - Contains the user if found, empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find a user by their ID
     * 
     * Retrieves a user from the database using their unique ID.
     * 
     * @param userId The user's unique identifier
     * @return Optional<User> - Contains the user if found, empty if not found
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Find a user by their email address
     * 
     * Retrieves a user from the database using their email.
     * 
     * @param email The email address to search for
     * @return Optional<User> - Contains the user if found, empty if not found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get all users in the system
     * 
     * WARNING: This method should only be used by administrators.
     * Retrieves all user accounts from the database.
     * 
     * @return List of all User objects in the system
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Update a user's balance (subtract amount)
     * 
     * Reduces a user's account balance by a specific amount.
     * Used when a user buys stocks (money deducted from balance).
     * 
     * @param user The user object to update
     * @param amount The amount to deduct (must be positive)
     * @throws IllegalArgumentException if amount would make balance negative
     */
    public void deductBalance(User user, BigDecimal amount) {
        // Check if user has sufficient balance
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + user.getBalance() + ", Required: " + amount);
        }
        
        // Subtract the amount from balance
        BigDecimal newBalance = user.getBalance().subtract(amount);
        user.setBalance(newBalance);
        
        // Save the updated user to database
        userRepository.save(user);
    }
    
    /**
     * Update a user's balance (add amount)
     * 
     * Increases a user's account balance by a specific amount.
     * Used when a user sells stocks (money added to balance).
     * 
     * @param user The user object to update
     * @param amount The amount to add (must be positive)
     */
    public void addBalance(User user, BigDecimal amount) {
        // Check that amount is positive
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // Add the amount to balance
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        
        // Save the updated user to database
        userRepository.save(user);
    }
    
    /**
     * Calculate a user's total portfolio value
     * 
     * This is a helper method to calculate the total worth of all stocks owned.
     * Would need to be implemented with real market price data.
     * 
     * @param user The user object
     * @return Total portfolio value in USD
     */
    public BigDecimal calculatePortfolioValue(User user) {
        // This would need to be implemented with current market prices
        // For now, return zero as a placeholder
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate a user's profit/loss
     * 
     * Compares current account value to initial balance.
     * 
     * @param user The user object
     * @return Profit (positive) or Loss (negative) amount
     */
    public BigDecimal calculateProfitLoss(User user) {
        // Current balance + portfolio value - initial balance
        BigDecimal currentValue = user.getBalance().add(calculatePortfolioValue(user));
        return currentValue.subtract(user.getInitialBalance());
    }
    
    /**
     * Deactivate a user account
     * 
     * Sets the user's account as inactive without deleting it.
     * This preserves the user's trading history and data.
     * 
     * @param user The user object to deactivate
     */
    public void deactivateUser(User user) {
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    /**
     * Delete a user permanently
     * 
     * WARNING: This operation is permanent and cannot be undone.
     * Deletes all user data from the database.
     * 
     * @param userId The ID of the user to delete
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
