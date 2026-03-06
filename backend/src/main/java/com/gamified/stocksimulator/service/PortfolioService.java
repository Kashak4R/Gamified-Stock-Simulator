package com.gamified.stocksimulator.service;

import com.gamified.stocksimulator.model.Portfolio;
import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PortfolioService Class - Business Logic Layer for Portfolio Management
 * 
 * This service handles all operations related to user stock holdings.
 * It manages buying and selling stocks, updating quantities, and maintaining portfolio records.
 * 
 * Key Responsibilities:
 *   - Add stocks to user portfolio (buying)
 *   - Remove stocks from portfolio (selling)
 *   - Update stock quantities and purchase prices
 *   - Retrieve user's portfolio information
 *   - Calculate portfolio statistics
 * 
 * Annotation Explanations:
 *   @Service - Marks this class as a service component (business logic layer)
 *   @Transactional - Ensures database operations are atomic (all or nothing)
 *   @Autowired - Automatically injects dependencies
 */
@Service
@Transactional
public class PortfolioService {
    
    /**
     * PortfolioRepository - Used to perform database operations on Portfolio entity
     * Automatically injected by Spring
     */
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    /**
     * Add a stock purchase to a user's portfolio
     * 
     * This method adds a new stock to the user's portfolio or updates existing holdings.
     * If the user already owns the stock, it updates the quantity and average purchase price.
     * 
     * Logic:
     *   1. Check if user already owns this stock
     *   2. If yes: Calculate new average purchase price and update quantity
     *   3. If no: Create a new portfolio record for this stock
     *   4. Save to database
     * 
     * Average Price Calculation Example:
     *   User bought 10 shares at $100 = $1000 total
     *   User buys 5 more at $120 = $600 total
     *   New average: ($1000 + $600) / (10 + 5) = $106.67 per share
     * 
     * @param user The user buying the stock
     * @param symbol The stock symbol (e.g., "AAPL")
     * @param quantity The number of shares being bought
     * @param purchasePrice The price per share paid
     * @return The updated Portfolio object
     */
    public Portfolio addStock(User user, String symbol, Integer quantity, BigDecimal purchasePrice) {
        // Try to find existing portfolio record for this stock
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndSymbol(user, symbol);
        
        Portfolio portfolio;
        
        if (existingPortfolio.isPresent()) {
            // User already owns this stock - update it
            portfolio = existingPortfolio.get();
            
            // Calculate current total investment
            BigDecimal currentTotal = portfolio.getPurchasePrice().multiply(BigDecimal.valueOf(portfolio.getQuantity()));
            
            // Calculate new total investment (add current purchase)
            BigDecimal newPurchaseTotal = purchasePrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal newTotal = currentTotal.add(newPurchaseTotal);
            
            // Calculate new total quantity
            Integer newQuantity = portfolio.getQuantity() + quantity;
            
            // Calculate new average purchase price
            BigDecimal newAveragePrice = newTotal.divide(BigDecimal.valueOf(newQuantity), 2, BigDecimal.ROUND_HALF_UP);
            
            // Update the portfolio record
            portfolio.setQuantity(newQuantity);
            portfolio.setPurchasePrice(newAveragePrice);
        } else {
            // User doesn't own this stock yet - create new record
            portfolio = new Portfolio();
            portfolio.setUser(user);
            portfolio.setSymbol(symbol);
            portfolio.setQuantity(quantity);
            portfolio.setPurchasePrice(purchasePrice);
            portfolio.setPurchaseDate(LocalDateTime.now());
        }
        
        // Save the portfolio to database
        return portfolioRepository.save(portfolio);
    }
    
    /**
     * Remove shares of a stock from user's portfolio
     * 
     * This method sells shares of a stock. If all shares are sold, the record is deleted.
     * 
     * Logic:
     *   1. Check if user owns this stock
     *   2. If not: Throw error (can't sell what you don't own)
     *   3. If yes: Reduce quantity by number of shares sold
     *   4. If quantity becomes 0: Delete the portfolio record
     *   5. If quantity > 0: Update and save the record
     * 
     * @param user The user selling the stock
     * @param symbol The stock symbol being sold
     * @param quantity The number of shares being sold
     * @throws IllegalArgumentException if user doesn't own this stock or insufficient shares
     */
    public void removeStock(User user, String symbol, Integer quantity) {
        // Find the portfolio record for this stock
        Optional<Portfolio> portfolio = portfolioRepository.findByUserAndSymbol(user, symbol);
        
        // Check if user owns this stock
        if (!portfolio.isPresent()) {
            throw new IllegalArgumentException("User does not own stock: " + symbol);
        }
        
        Portfolio existingPortfolio = portfolio.get();
        
        // Check if user has enough shares to sell
        if (existingPortfolio.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                "Insufficient shares. Owned: " + existingPortfolio.getQuantity() + 
                ", Selling: " + quantity
            );
        }
        
        // Calculate remaining quantity after sale
        Integer remainingQuantity = existingPortfolio.getQuantity() - quantity;
        
        if (remainingQuantity == 0) {
            // User sold all shares - delete the portfolio record
            portfolioRepository.deleteByUserAndSymbol(user, symbol);
        } else {
            // User still owns some shares - update the record
            existingPortfolio.setQuantity(remainingQuantity);
            portfolioRepository.save(existingPortfolio);
        }
    }
    
    /**
     * Get all stocks owned by a user
     * 
     * Returns a list of all portfolio records for a user.
     * Useful for displaying the user's portfolio dashboard.
     * 
     * @param user The user whose portfolio we want
     * @return List of all Portfolio objects owned by this user
     */
    public List<Portfolio> getUserPortfolio(User user) {
        return portfolioRepository.findByUser(user);
    }
    
    /**
     * Get a user's holdings for a specific stock
     * 
     * Returns information about how many shares of a specific stock the user owns.
     * 
     * @param user The user object
     * @param symbol The stock symbol to look up
     * @return Optional<Portfolio> - Contains the portfolio record if user owns this stock
     */
    public Optional<Portfolio> getStockHolding(User user, String symbol) {
        return portfolioRepository.findByUserAndSymbol(user, symbol);
    }
    
    /**
     * Check if a user owns a specific stock
     * 
     * Simple boolean check - returns true if user owns this stock, false otherwise.
     * 
     * @param user The user object
     * @param symbol The stock symbol to check
     * @return true if user owns this stock, false otherwise
     */
    public boolean doesUserOwnStock(User user, String symbol) {
        return portfolioRepository.findByUserAndSymbol(user, symbol).isPresent();
    }
    
    /**
     * Get the number of different stocks in a user's portfolio
     * 
     * Returns the count of unique stocks owned by a user.
     * Example: User owns AAPL, GOOGL, MSFT = returns 3
     * 
     * @param user The user object
     * @return Number of different stocks owned
     */
    public long getPortfolioStockCount(User user) {
        return portfolioRepository.countByUser(user);
    }
    
    /**
     * Get the total number of shares owned by a user
     * 
     * Adds up all shares across all stocks.
     * Example: 10 AAPL + 5 GOOGL + 20 MSFT = 35 total shares
     * 
     * @param user The user object
     * @return Total number of shares owned
     */
    public Integer getTotalShareCount(User user) {
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);
        return portfolios.stream()
                .mapToInt(Portfolio::getQuantity)
                .sum();
    }
    
    /**
     * Calculate total investment in stocks
     * 
     * Calculates the total amount spent on all current holdings.
     * = Sum of (purchase_price * quantity) for all stocks
     * 
     * @param user The user object
     * @return Total amount invested in stocks
     */
    public BigDecimal calculateTotalInvestment(User user) {
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);
        return portfolios.stream()
                .map(p -> p.getPurchasePrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
