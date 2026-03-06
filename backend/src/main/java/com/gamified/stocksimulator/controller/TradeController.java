package com.gamified.stocksimulator.controller;

import com.gamified.stocksimulator.model.Trade;
import com.gamified.stocksimulator.model.User;
import com.gamified.stocksimulator.service.TradeService;
import com.gamified.stocksimulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TradeController Class - REST API Endpoints for Trading Operations
 * 
 * This controller handles HTTP requests related to buying and selling stocks.
 * It receives trade requests from the frontend and delegates to TradeService for processing.
 * 
 * Key Endpoints:
 *   - POST /api/trades/buy - Execute a buy trade
 *   - POST /api/trades/sell - Execute a sell trade
 *   - GET /api/trades/user/{userId} - Get user's trading history
 *   - GET /api/trades/user/{userId}/stock/{symbol} - Get trades for specific stock
 * 
 * Trade Flow:
 *   1. Frontend sends buy/sell request with stock symbol, quantity, and price
 *   2. Controller validates the request
 *   3. TradeService executes the trade
 *   4. Response sent back to frontend with status and details
 * 
 * Annotation Explanations:
 *   @RestController - Marks this as a REST API controller
 *   @RequestMapping - Sets base URL path for all endpoints
 *   @PostMapping/@GetMapping - Defines HTTP method and specific endpoint path
 *   @PathVariable - Extracts values from URL path
 *   @RequestBody - Receives data in request body (usually JSON)
 */
@RestController
@RequestMapping("/api/trades")
public class TradeController {
    
    /**
     * TradeService - Injected by Spring to handle trade logic
     */
    @Autowired
    private TradeService tradeService;
    
    /**
     * UserService - Injected by Spring to access user data
     */
    @Autowired
    private UserService userService;
    
    /**
     * Execute a BUY trade - POST /api/trades/buy
     * 
     * This endpoint allows users to buy stocks.
     * The system will deduct money from balance and add stocks to portfolio.
     * 
     * Request Body Example:
     * {
     *   "userId": 1,
     *   "symbol": "AAPL",
     *   "quantity": 10,
     *   "price": 150.25
     * }
     * 
     * Response: Trade confirmation with details
     * 
     * @param requestBody Map containing userId, symbol, quantity, price
     * @return ResponseEntity with trade details or error message
     */
    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract data from request body
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String symbol = (String) requestBody.get("symbol");
            Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
            BigDecimal price = new BigDecimal(requestBody.get("price").toString());
            
            // Validate inputs
            if (symbol == null || symbol.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Stock symbol is required"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quantity must be greater than 0"));
            }
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Price must be greater than 0"));
            }
            
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Refresh user object to get latest balance
            User currentUser = user.get();
            
            // Execute the buy trade
            // If there's insufficient balance, the service will throw an exception
            Trade trade = tradeService.executeBuyTrade(currentUser, symbol, quantity, price);
            
            // Calculate total cost
            BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
            
            // Return success response
            return ResponseEntity.ok(Map.of(
                "tradeId", trade.getTradeId(),
                "userId", currentUser.getUserId(),
                "symbol", symbol,
                "type", "BUY",
                "quantity", quantity,
                "pricePerShare", price,
                "totalCost", totalCost,
                "status", "COMPLETED",
                "message", "Trade executed successfully"
            ));
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors (insufficient balance, etc.)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.internalServerError().body(Map.of("error", "Trade execution failed: " + e.getMessage()));
        }
    }
    
    /**
     * Execute a SELL trade - POST /api/trades/sell
     * 
     * This endpoint allows users to sell stocks from their portfolio.
     * The system will add money to balance and remove stocks from portfolio.
     * 
     * Request Body Example:
     * {
     *   "userId": 1,
     *   "symbol": "AAPL",
     *   "quantity": 5,
     *   "price": 155.50
     * }
     * 
     * Response: Trade confirmation with details
     * 
     * @param requestBody Map containing userId, symbol, quantity, price
     * @return ResponseEntity with trade details or error message
     */
    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract data from request body
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            String symbol = (String) requestBody.get("symbol");
            Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
            BigDecimal price = new BigDecimal(requestBody.get("price").toString());
            
            // Validate inputs
            if (symbol == null || symbol.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Stock symbol is required"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quantity must be greater than 0"));
            }
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Price must be greater than 0"));
            }
            
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Refresh user object to get latest balance
            User currentUser = user.get();
            
            // Execute the sell trade
            // If user doesn't own enough shares, the service will throw an exception
            Trade trade = tradeService.executeSellTrade(currentUser, symbol, quantity, price);
            
            // Calculate total proceeds
            BigDecimal totalProceeds = price.multiply(BigDecimal.valueOf(quantity));
            
            // Return success response
            return ResponseEntity.ok(Map.of(
                "tradeId", trade.getTradeId(),
                "userId", currentUser.getUserId(),
                "symbol", symbol,
                "type", "SELL",
                "quantity", quantity,
                "pricePerShare", price,
                "totalProceeds", totalProceeds,
                "status", "COMPLETED",
                "message", "Trade executed successfully"
            ));
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors (insufficient shares, etc.)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.internalServerError().body(Map.of("error", "Trade execution failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get user's complete trading history - GET /api/trades/user/{userId}
     * 
     * This endpoint returns all trades (buy and sell) made by a user.
     * Results are sorted by most recent first.
     * 
     * URL Example: GET /api/trades/user/1
     * 
     * Response: List of all trades
     * 
     * @param userId The user ID from the URL path
     * @return ResponseEntity with list of trades or error message
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTradeHistory(@PathVariable Long userId) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get trade history from service
            List<Trade> trades = tradeService.getUserTradeHistory(user.get());
            
            // Convert to response format
            List<Map<String, Object>> tradeList = trades.stream().map(trade -> {
                Map<String, Object> map = new HashMap<>();
                map.put("tradeId", trade.getTradeId());
                map.put("symbol", trade.getSymbol());
                map.put("type", trade.getType());
                map.put("quantity", trade.getQuantity());
                map.put("price", trade.getPrice());
                map.put("totalAmount", trade.getTotalAmount());
                map.put("executedAt", trade.getExecutedAt());
                return map;
            }).toList();
            
            // Return success response
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "totalTrades", tradeList.size(),
                "trades", tradeList
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve trade history"));
        }
    }
    
    /**
     * Get trades for a specific stock - GET /api/trades/user/{userId}/stock/{symbol}
     * 
     * This endpoint returns all trades for a specific stock made by a user.
     * Useful for analyzing trading activity on a single stock.
     * 
     * URL Example: GET /api/trades/user/1/stock/AAPL
     * 
     * Response: List of trades for the specified stock
     * 
     * @param userId The user ID from the URL path
     * @param symbol The stock symbol from the URL path
     * @return ResponseEntity with list of trades or error message
     */
    @GetMapping("/user/{userId}/stock/{symbol}")
    public ResponseEntity<?> getStockTradeHistory(@PathVariable Long userId, @PathVariable String symbol) {
        try {
            // Get user from database
            Optional<User> user = userService.getUserById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get stock-specific trade history from service
            List<Trade> trades = tradeService.getStockTradeHistory(user.get(), symbol);
            
            // Convert to response format
            List<Map<String, Object>> tradeList = trades.stream().map(trade -> {
                Map<String, Object> map = new HashMap<>();
                map.put("tradeId", trade.getTradeId());
                map.put("type", trade.getType());
                map.put("quantity", trade.getQuantity());
                map.put("price", trade.getPrice());
                map.put("totalAmount", trade.getTotalAmount());
                map.put("executedAt", trade.getExecutedAt());
                return map;
            }).toList();
            
            // Return success response
            return ResponseEntity.ok(Map.of(
                "symbol", symbol,
                "totalTrades", tradeList.size(),
                "trades", tradeList
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve stock trade history"));
        }
    }
}
