package com.gamified.stocksimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Trade Entity Class - Represents a buy/sell transaction in the system
 * 
 * This entity logs every trade (buy or sell) made by a user.
 * It serves as an audit trail showing transaction history.
 * 
 * Example: User bought 10 AAPL shares at $150 each on 2024-01-15
 * - symbol = 'AAPL'
 * - type = 'BUY'
 * - quantity = 10
 * - price = 150.00
 * - totalAmount = 1500.00
 */
@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    
    /**
     * Unique identifier for this trade transaction
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;
    
    /**
     * Reference to the user who made this trade
     * Links the trade to the person who bought/sold
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Stock symbol/ticker - Examples: AAPL, GOOGL, MSFT
     * Identifies which stock was traded
     */
    @Column(nullable = false, length = 10)
    private String symbol;
    
    /**
     * Trade type - Either 'BUY' or 'SELL'
     * Specifies whether the user bought or sold stock
     */
    @Column(nullable = false, length = 4)
    private String type; // BUY or SELL
    
    /**
     * Number of shares traded in this transaction
     * Example: 10 means 10 shares were bought or sold
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Price per share at the time of trade
     * Example: $150.25 per share
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    /**
     * Total amount of the trade (quantity * price)
     * Example: 10 shares * $150.00 = $1500.00
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    /**
     * Trade status - 'PENDING', 'COMPLETED', or 'FAILED'
     * Tracks whether the trade was successfully executed
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    
    /**
     * Timestamp when the trade was executed
     */
    @Column(nullable = false)
    private LocalDateTime executedAt;
    
    /**
     * Timestamp when this record was created in the system
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * PrePersist - Called automatically before saving to database
     * Initializes timestamps
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (executedAt == null) {
            executedAt = LocalDateTime.now();
        }
    }
}
