package com.gamified.stocksimulator.service;

import com.gamified.stocksimulator.model.Portfolio;
import com.gamified.stocksimulator.model.Trade;
import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TradeService Class - Business Logic Layer for Trading Operations
 * 
 * This service handles all trading-related operations in the stock simulator.
 * It coordinates with Portfolio and User services to execute buy/sell trades.
 * 
 * Key Responsibilities:
 *   - Execute buy trades (deduct money, update portfolio)
 *   - Execute sell trades (add money, update portfolio)
 *   - Record trade transactions in database
 *   - Validate trade operations
 *   - Generate trade history and analytics
 * 
 * Trade Flow:
 *   1. User submits a trade request (buy/sell)
 *   2. TradeService validates the request
 *   3. UserService updates user balance
 *   4. PortfolioService updates stock holdings
 *   5. TradeService records the transaction
 * 
 * Annotation Explanations:
 *   @Service - Marks this class as a service component (business logic layer)
 *   @Transactional - Ensures database operations are atomic (all or nothing)
 *   @Autowired - Automatically injects dependencies
 */
@Service
@Transactional
public class TradeService {
    
    /**
     * TradeRepository - Used to perform database operations on Trade entity
     * Automatically injected by Spring
     */
    @Autowired
    private TradeRepository tradeRepository;
    
    /**
     * UserService - Used to manage user balance updates
     * Automatically injected by Spring
     */
    @Autowired
    private UserService userService;
    
    /**
     * PortfolioService - Used to manage stock holdings
     * Automatically injected by Spring
     */
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * Execute a BUY trade - User purchases stocks
     * 
     * This method handles the complete flow of a buy trade:
     * 1. Check if user has sufficient balance
     * 2. Deduct the total amount from user's account balance
     * 3. Add stocks to user's portfolio
     * 4. Record the trade transaction in database
     * 5. Set trade status to COMPLETED
     * 
     * The @Transactional annotation ensures that if any step fails, 
     * all changes are rolled back (database is not partially updated).
     * 
     * @param user The user making the purchase
     * @param symbol The stock symbol being bought (e.g., "AAPL")
     * @param quantity The number of shares to buy
     * @param price The price per share at the time of purchase
     * @return The Trade object representing this transaction
     * @throws IllegalArgumentException if user has insufficient balance
     */
    public Trade executeBuyTrade(User user, String symbol, Integer quantity, BigDecimal price) {
        // Calculate total cost of the purchase (quantity * price)
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        
        // Check if user has sufficient balance
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new IllegalArgumentException(
                "Insufficient balance. Required: $" + totalCost + 
                ", Available: $" + user.getBalance()
            );
        }
        
        // Step 1: Deduct money from user's account
        userService.deductBalance(user, totalCost);
        
        // Step 2: Add stocks to user's portfolio
        // This creates a new portfolio record or updates existing one
        portfolioService.addStock(user, symbol, quantity, price);
        
        // Step 3: Create a trade record for audit trail
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setSymbol(symbol);
        trade.setType("BUY");  // Specifies this is a buy trade
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTotalAmount(totalCost);
        trade.setStatus("COMPLETED");
        trade.setExecutedAt(LocalDateTime.now());
        
        // Step 4: Save the trade to database
        return tradeRepository.save(trade);
    }
    
    /**
     * Execute a SELL trade - User sells stocks
     * 
     * This method handles the complete flow of a sell trade:
     * 1. Check if user owns the stock and has enough shares
     * 2. Remove stocks from user's portfolio
     * 3. Add proceeds to user's account balance
     * 4. Record the trade transaction in database
     * 5. Set trade status to COMPLETED
     * 
     * The @Transactional annotation ensures that if any step fails,
     * all changes are rolled back (database is not partially updated).
     * 
     * @param user The user selling stocks
     * @param symbol The stock symbol being sold (e.g., "AAPL")
     * @param quantity The number of shares to sell
     * @param price The price per share at the time of sale
     * @return The Trade object representing this transaction
     * @throws IllegalArgumentException if user doesn't own the stock or has insufficient shares
     */
    public Trade executeSellTrade(User user, String symbol, Integer quantity, BigDecimal price) {
        // Calculate total proceeds from the sale (quantity * price)
        BigDecimal totalProceeds = price.multiply(BigDecimal.valueOf(quantity));
        
        // Check if user owns this stock and has enough shares
        // The portfolioService will throw an exception if checks fail
        portfolioService.removeStock(user, symbol, quantity);
        
        // Step 1: Add proceeds to user's account balance
        userService.addBalance(user, totalProceeds);
        
        // Step 2: Create a trade record for audit trail
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setSymbol(symbol);
        trade.setType("SELL");  // Specifies this is a sell trade
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setTotalAmount(totalProceeds);
        trade.setStatus("COMPLETED");
        trade.setExecutedAt(LocalDateTime.now());
        
        // Step 3: Save the trade to database
        return tradeRepository.save(trade);
    }
    
    /**
     * Get all trades made by a user (sorted by most recent first)
     * 
     * Returns the complete trading history for a user.
     * Useful for displaying user's transaction history.
     * 
     * @param user The user whose trades we want
     * @return List of all trades made by this user, most recent first
     */
    public List<Trade> getUserTradeHistory(User user) {
        return tradeRepository.findByUserOrderByExecutedAtDesc(user);
    }
    
    /**
     * Get all trades for a specific stock by a user
     * 
     * Returns all buy/sell transactions for a particular stock.
     * Useful for analyzing trading activity on a specific stock.
     * 
     * @param user The user object
     * @param symbol The stock symbol (e.g., "AAPL")
     * @return List of trades for this symbol by this user, most recent first
     */
    public List<Trade> getStockTradeHistory(User user, String symbol) {
        return tradeRepository.findByUserAndSymbolOrderByExecutedAtDesc(user, symbol);
    }
    
    /**
     * Get all buy trades made by a user
     * 
     * Returns only the purchases made by a user (not sales).
     * Useful for analyzing buying patterns.
     * 
     * @param user The user object
     * @return List of all buy trades made by this user, most recent first
     */
    public List<Trade> getUserBuyTrades(User user) {
        return tradeRepository.findByUserAndTypeOrderByExecutedAtDesc(user, "BUY");
    }
    
    /**
     * Get all sell trades made by a user
     * 
     * Returns only the sales made by a user (not purchases).
     * Useful for analyzing selling patterns.
     * 
     * @param user The user object
     * @return List of all sell trades made by this user, most recent first
     */
    public List<Trade> getUserSellTrades(User user) {
        return tradeRepository.findByUserAndTypeOrderByExecutedAtDesc(user, "SELL");
    }
    
    /**
     * Calculate total profit/loss on a specific stock
     * 
     * Compares the current price to the average purchase price.
     * Formula: (Current Price - Average Purchase Price) * Quantity Held
     * 
     * @param user The user object
     * @param symbol The stock symbol
     * @param currentPrice The current market price
     * @return Profit (positive) or Loss (negative) in dollars
     */
    public BigDecimal calculateStockProfitLoss(User user, String symbol, BigDecimal currentPrice) {
        // Get the user's portfolio record for this stock
        java.util.Optional<Portfolio> portfolio = portfolioService.getStockHolding(user, symbol);
        
        if (!portfolio.isPresent()) {
            return BigDecimal.ZERO;  // User doesn't own this stock
        }
        
        // Calculate profit/loss
        // = (current price - purchase price) * quantity
        Portfolio p = portfolio.get();
        BigDecimal priceDifference = currentPrice.subtract(p.getPurchasePrice());
        return priceDifference.multiply(BigDecimal.valueOf(p.getQuantity()));
    }
    
    /**
     * Get total number of trades made by a user
     * 
     * Returns the count of all buy and sell transactions.
     * 
     * @param user The user object
     * @return Total number of trades executed
     */
    public long getTotalTradeCount(User user) {
        List<Trade> allTrades = tradeRepository.findByUserOrderByExecutedAtDesc(user);
        return allTrades.size();
    }
}
