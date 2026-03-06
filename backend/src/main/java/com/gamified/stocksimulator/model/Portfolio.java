package com.gamified.stocksimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Portfolio Entity Class - Represents a user's stock holdings
 * 
 * This entity tracks what stocks a user owns and how many shares they hold.
 * The portfolio is mapped to the 'portfolios' table in the database.
 * 
 * Example: If a user owns 10 shares of AAPL, there will be one portfolio record
 * with symbol='AAPL', quantity=10, purchasePrice=(average price paid)
 */
@Entity
@Table(name = "portfolios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    
    /**
     * Unique identifier for this portfolio record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioId;
    
    /**
     * Reference to the user who owns this stock
     * ManyToOne means many portfolio records can belong to one user
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Stock symbol/ticker - Examples: AAPL, GOOGL, MSFT
     * Represents which stock is being held
     */
    @Column(nullable = false, length = 10)
    private String symbol;
    
    /**
     * Number of shares the user owns of this stock
     * Example: 10 shares of AAPL
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Average price paid per share
     * Used to calculate profit/loss when selling
     * Example: User bought 10 shares at $150 each, purchasePrice = 150.00
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchasePrice;
    
    /**
     * Timestamp when this stock was first purchased
     */
    @Column(nullable = false)
    private LocalDateTime purchaseDate;
    
    /**
     * Timestamp when this portfolio record was created in the system
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp of the last update to this portfolio record
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * PrePersist - Called before saving to database for the first time
     * Initializes creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * PreUpdate - Called before updating in database
     * Updates the last modified timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
