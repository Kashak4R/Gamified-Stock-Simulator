package com.gamified.stocksimulator.controller;

import com.gamified.stocksimulator.model.Portfolio;
import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.service.PortfolioService;
import com.gamified.stocksimulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PortfolioController Class - REST API Endpoints for Portfolio Management
 * 
 * This controller handles HTTP requests related to user stock holdings (portfolio).
 * It provides endpoints to view, manage, and analyze stock positions.
 * 
 * Key Endpoints:
 *   - GET /api/portfolio/user/{userId} - Get complete portfolio
 *   - GET /api/portfolio/user/{userId}/stock/{symbol} - Get specific holding
 *   - GET /api/portfolio/user/{userId}/value - Get portfolio statistics
 * 
 * Annotation Explanations:
 *   @RestController - Marks this as a REST API controller
 *   @RequestMapping - Sets base URL path for all endpoints
 *   @GetMapping - Defines GET HTTP method and specific endpoint path
 *   @PathVariable - Extracts values from URL path (e.g., {userId})
 */
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    
    /**
     * PortfolioService - Injected by Spring to handle portfolio logic
     */
    @Autowired
    private PortfolioService portfolioService;
    
    /**
     * UserService - Injected by Spring to access user data
     */
    @Autowired
    private UserService userService;
    
    /**
     * Get user's complete portfolio - GET /api/portfolio/user/{userId}
     * 
     * This endpoint returns all stocks owned by a user with their quantities
     * and purchase prices.
     * 
     * URL Example: GET /api/portfolio/user/1
     * 
     * Response Example:
     * {
     *   "userId": 1,
     *   "totalStocks": 2,
     *   "portfolio": [
     *     {
     *       "symbol": "AAPL",
     *       "quantity": 10,
     *       "purchasePrice": 150.00,
     *       "purchaseDate": "2024-01-15T10:30:00"
     *     },
     *     {
     *       "symbol": "GOOGL",
     *       "quantity": 5,
     *       "purchasePrice": 120.00,
     *       "purchaseDate": "2024-01-20T14:15:00"
     *     }
     *   ]
     * }
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with portfolio details or error message
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPortfolio(@PathVariable Long userId) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get portfolio from service
            List<Portfolio> portfolios = portfolioService.getUserPortfolio(user.get());
            
            // Convert to response format (remove unnecessary fields)
            List<Map<String, Object>> portfolioList = portfolios.stream().map(portfolio -> {
                Map<String, Object> map = new HashMap<>();
                map.put("portfolioId", portfolio.getPortfolioId());
                map.put("symbol", portfolio.getSymbol());
                map.put("quantity", portfolio.getQuantity());
                map.put("purchasePrice", portfolio.getPurchasePrice());
                map.put("purchaseDate", portfolio.getPurchaseDate());
                return map;
            }).collect(Collectors.toList());
            
            // Return success response
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "totalStocks", portfolioList.size(),
                "portfolio", portfolioList
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve portfolio"));
        }
    }
    
    /**
     * Get user's holdings for a specific stock - GET /api/portfolio/user/{userId}/stock/{symbol}
     * 
     * This endpoint returns information about a user's holdings of a specific stock.
     * Returns 404 if user doesn't own that stock.
     * 
     * URL Example: GET /api/portfolio/user/1/stock/AAPL
     * 
     * Response Example:
     * {
     *   "symbol": "AAPL",
     *   "quantity": 10,
     *   "purchasePrice": 150.00,
     *   "purchaseDate": "2024-01-15T10:30:00"
     * }
     * 
     * @param userId The user ID from the URL path
     * @param symbol The stock symbol from the URL path
     * @return ResponseEntity with stock holding details or error message
     */
    @GetMapping("/user/{userId}/stock/{symbol}")
    public ResponseEntity<?> getStockHolding(@PathVariable Long userId, @PathVariable String symbol) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get stock holding from service
            Optional<Portfolio> portfolio = portfolioService.getStockHolding(user.get(), symbol);
            
            // Check if user owns this stock
            if (!portfolio.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Return stock holding details
            Portfolio p = portfolio.get();
            return ResponseEntity.ok(Map.of(
                "symbol", p.getSymbol(),
                "quantity", p.getQuantity(),
                "purchasePrice", p.getPurchasePrice(),
                "purchaseDate", p.getPurchaseDate()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve stock holding"));
        }
    }
    
    /**
     * Get portfolio statistics and value - GET /api/portfolio/user/{userId}/stats
     * 
     * This endpoint returns various statistics about the user's portfolio including
     * number of stocks, total shares, and total invested amount.
     * 
     * URL Example: GET /api/portfolio/user/1/stats
     * 
     * Response Example:
     * {
     *   "userId": 1,
     *   "totalStocks": 2,
     *   "totalShares": 15,
     *   "totalInvested": 1800.00
     * }
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with portfolio statistics or error message
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getPortfolioStats(@PathVariable Long userId) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Calculate portfolio statistics
            long totalStocks = portfolioService.getPortfolioStockCount(user.get());
            Integer totalShares = portfolioService.getTotalShareCount(user.get());
            var totalInvested = portfolioService.calculateTotalInvestment(user.get());
            
            // Return statistics
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "totalStocks", totalStocks,
                "totalShares", totalShares,
                "totalInvested", totalInvested
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to calculate portfolio statistics"));
        }
    }
    
    /**
     * Check if user owns a specific stock - GET /api/portfolio/user/{userId}/owns/{symbol}
     * 
     * This endpoint is a simple boolean check to determine if a user owns a stock.
     * Returns true if owned, false if not.
     * 
     * URL Example: GET /api/portfolio/user/1/owns/AAPL
     * 
     * Response Example:
     * {
     *   "symbol": "AAPL",
     *   "isOwned": true
     * }
     * 
     * @param userId The user ID from the URL path
     * @param symbol The stock symbol from the URL path
     * @return ResponseEntity with boolean result or error message
     */
    @GetMapping("/user/{userId}/owns/{symbol}")
    public ResponseEntity<?> checkOwnership(@PathVariable Long userId, @PathVariable String symbol) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Check if user owns this stock
            boolean isOwned = portfolioService.doesUserOwnStock(user.get(), symbol);
            
            // Return result
            return ResponseEntity.ok(Map.of(
                "symbol", symbol,
                "isOwned", isOwned
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to check ownership"));
        }
    }
}
