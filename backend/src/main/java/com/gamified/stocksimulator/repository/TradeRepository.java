package com.gamified.stocksimulator.repository;

import com.gamified.stocksimulator.model.Trade;
import com.gamified.stocksimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TradeRepository Interface - Database Access Layer for Trade Entity
 * 
 * This repository handles all database operations for trade transactions.
 * It provides methods to retrieve, save, and manage trade history.
 * 
 * Key Points:
 *   - JpaRepository<Trade, Long> - Generic parameters: Entity class, Primary key type
 *   - Used for querying transaction history and trade records
 *   - Spring Data JPA automatically implements these methods based on naming conventions
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    /**
     * Find all trades made by a specific user
     * 
     * Returns the complete trading history for a user.
     * Useful for displaying user's transaction history.
     * 
     * @param user The user object whose trades we want to find
     * @return List of all trades made by this user (sorted by most recent first if needed)
     * 
     * SQL equivalent: SELECT * FROM trades WHERE user_id = ? ORDER BY executed_at DESC
     */
    List<Trade> findByUserOrderByExecutedAtDesc(User user);
    
    /**
     * Find all trades for a specific stock by a user
     * 
     * Returns all buy/sell transactions for a particular stock by a user.
     * Useful for analyzing trading activity on a specific stock.
     * 
     * @param user The user object
     * @param symbol The stock symbol (e.g., "AAPL")
     * @return List of trades for this symbol by this user
     * 
     * SQL equivalent: SELECT * FROM trades WHERE user_id = ? AND symbol = ? ORDER BY executed_at DESC
     */
    List<Trade> findByUserAndSymbolOrderByExecutedAtDesc(User user, String symbol);
    
    /**
     * Find all trades of a specific type (BUY or SELL)
     * 
     * Returns all buy trades or all sell trades for a user.
     * Useful for analyzing buying vs selling behavior.
     * 
     * @param user The user object
     * @param type The trade type ("BUY" or "SELL")
     * @return List of trades matching the specified type
     * 
     * SQL equivalent: SELECT * FROM trades WHERE user_id = ? AND type = ? ORDER BY executed_at DESC
     */
    List<Trade> findByUserAndTypeOrderByExecutedAtDesc(User user, String type);
    
    /**
     * Find all trades within a specific time period
     * 
     * Returns all trades made between startDate and endDate.
     * Useful for generating trading reports for a specific period.
     * 
     * @param user The user object
     * @param startDate The start date/time
     * @param endDate The end date/time
     * @return List of trades executed within the time period
     * 
     * SQL equivalent: SELECT * FROM trades WHERE user_id = ? AND executed_at BETWEEN ? AND ? ORDER BY executed_at DESC
     */
    List<Trade> findByUserAndExecutedAtBetweenOrderByExecutedAtDesc(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count completed trades by a user for a specific stock
     * 
     * Returns the number of completed transactions for a stock.
     * Useful for analyzing trading frequency.
     * 
     * @param user The user object
     * @param symbol The stock symbol
     * @param status The trade status (e.g., "COMPLETED")
     * @return Count of trades matching criteria
     * 
     * SQL equivalent: SELECT COUNT(*) FROM trades WHERE user_id = ? AND symbol = ? AND status = ?
     */
    long countByUserAndSymbolAndStatus(User user, String symbol, String status);
}
