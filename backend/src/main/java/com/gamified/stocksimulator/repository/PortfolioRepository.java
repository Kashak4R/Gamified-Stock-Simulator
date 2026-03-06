package com.gamified.stocksimulator.repository;

import com.gamified.stocksimulator.model.Portfolio;
import com.gamified.stocksimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * PortfolioRepository Interface - Database Access Layer for Portfolio Entity
 * 
 * This repository handles database operations for user stock holdings.
 * It provides methods to query, save, and delete portfolio records.
 * 
 * Key Points:
 *   - JpaRepository<Portfolio, Long> - Generic parameters: Entity class, Primary key type
 *   - Extends JpaRepository to get basic CRUD operations
 *   - Custom methods are implemented automatically by Spring Data JPA
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    /**
     * Find all portfolio records (stocks) owned by a specific user
     * 
     * This returns all the different stocks a user owns.
     * Example: If user owns AAPL and GOOGL, this returns both records.
     * 
     * @param user The user object whose portfolio we want to find
     * @return List of Portfolio objects owned by this user
     * 
     * SQL equivalent: SELECT * FROM portfolios WHERE user_id = ?
     */
    List<Portfolio> findByUser(User user);
    
    /**
     * Find a user's holdings for a specific stock symbol
     * 
     * This returns the portfolio record for a specific stock owned by a user.
     * Useful when we want to check if a user owns a particular stock.
     * 
     * @param user The user object
     * @param symbol The stock symbol to search for (e.g., "AAPL")
     * @return Optional<Portfolio> - Contains the portfolio record if user owns this stock
     * 
     * SQL equivalent: SELECT * FROM portfolios WHERE user_id = ? AND symbol = ?
     */
    Optional<Portfolio> findByUserAndSymbol(User user, String symbol);
    
    /**
     * Delete a user's holdings for a specific stock
     * 
     * This is used when a user sells all shares of a stock.
     * If a user sells their last share, we delete the portfolio record.
     * 
     * @param user The user object
     * @param symbol The stock symbol to delete
     * 
     * SQL equivalent: DELETE FROM portfolios WHERE user_id = ? AND symbol = ?
     */
    void deleteByUserAndSymbol(User user, String symbol);
    
    /**
     * Get the count of different stocks owned by a user
     * 
     * Returns how many different stocks a user has in their portfolio.
     * Example: If user owns AAPL and GOOGL, this returns 2.
     * 
     * @param user The user object
     * @return Number of different stocks owned by this user
     * 
     * SQL equivalent: SELECT COUNT(*) FROM portfolios WHERE user_id = ?
     */
    long countByUser(User user);
}
