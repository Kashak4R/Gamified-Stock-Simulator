-- SQL Script for Stock Simulator Database
-- 
-- This script creates all necessary tables for the Gamified Stock Market Simulator
-- Execute this script in MySQL to set up the database schema
--
-- Database Structure:
-- 1. users - stores user account information
-- 2. portfolios - stores user stock holdings
-- 3. trades - audit log of all buy/sell transactions

-- ============================================
-- CREATE DATABASE
-- ============================================

-- Drop existing database if it exists (useful for fresh starts)
DROP DATABASE IF EXISTS stock_simulator;

-- Create the main database for this application
CREATE DATABASE stock_simulator;

-- Use the newly created database for subsequent commands
USE stock_simulator;

-- ============================================
-- CREATE USERS TABLE
-- ============================================

-- The users table stores user account information
-- Each row represents one user account
CREATE TABLE users (
    -- user_id: Unique identifier for each user (auto-incremented)
    -- INT NOT NULL AUTO_INCREMENT PRIMARY KEY:
    --   - INT: Integer type (can store numbers)
    --   - NOT NULL: This field must always have a value (can't be empty)
    --   - AUTO_INCREMENT: Automatically generates sequential numbers (1, 2, 3, ...)
    --   - PRIMARY KEY: Uniquely identifies each row
    user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- username: The login name chosen by the user
    -- VARCHAR(50): Can store up to 50 characters
    -- NOT NULL: Username is required
    -- UNIQUE: No two users can have the same username
    username VARCHAR(50) NOT NULL UNIQUE,
    
    -- email: User's email address
    -- VARCHAR(100): Can store up to 100 characters (emails can be long)
    -- NOT NULL: Email is required
    -- UNIQUE: No two users can have the same email
    email VARCHAR(100) NOT NULL UNIQUE,
    
    -- password: Encrypted password (never store plain text!)
    -- VARCHAR(255): Hashed passwords are longer, so we need 255 characters
    -- NOT NULL: Password is required
    password VARCHAR(255) NOT NULL,
    
    -- balance: Current cash available for trading
    -- DECIMAL(19,2): 
    --   - 19 = total number of digits
    --   - 2 = number of decimal places (cents)
    --   - Example: 12345678901234567.89 (17 digits before decimal, 2 after)
    -- NOT NULL: Balance is required
    balance DECIMAL(19,2) NOT NULL,
    
    -- initial_balance: Starting balance when account was created
    -- DECIMAL(19,2): Same as balance
    -- NOT NULL: Initial balance is required
    initial_balance DECIMAL(19,2) NOT NULL,
    
    -- is_active: Whether the account is active or deactivated
    -- BOOLEAN: true=active, false=inactive
    -- NOT NULL: Status is required
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- created_at: When the user account was created
    -- TIMESTAMP: Date and time
    -- NOT NULL: Creation timestamp is required
    -- DEFAULT CURRENT_TIMESTAMP: Automatically set to current date/time when created
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- updated_at: When the user account was last modified
    -- TIMESTAMP: Date and time
    -- NOT NULL: Update timestamp is required
    -- DEFAULT CURRENT_TIMESTAMP: Initial value
    -- ON UPDATE CURRENT_TIMESTAMP: Automatically update to current time when modified
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- CREATE PORTFOLIOS TABLE
-- ============================================

-- The portfolios table stores what stocks each user owns
-- Each row represents one user's holding of one stock
-- Example: User 1 owning 10 shares of AAPL = 1 row
CREATE TABLE portfolios (
    -- portfolio_id: Unique identifier for each portfolio record
    -- INT NOT NULL AUTO_INCREMENT PRIMARY KEY: Auto-generated sequential ID
    portfolio_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- user_id: Links this portfolio to a specific user
    -- INT NOT NULL: User ID is required
    -- FOREIGN KEY: Value must exist in users table (ensures data integrity)
    -- ON DELETE CASCADE: If user is deleted, delete their portfolio records too
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- symbol: Stock ticker symbol
    -- VARCHAR(10): Can store symbols like AAPL, GOOGL, MSFT (usually 1-5 chars)
    -- NOT NULL: Symbol is required
    symbol VARCHAR(10) NOT NULL,
    
    -- quantity: Number of shares owned
    -- INT NOT NULL: Number of shares is required
    quantity INT NOT NULL,
    
    -- purchase_price: Average price paid per share
    -- DECIMAL(19,2): Can store currency values with cents
    -- NOT NULL: Purchase price is required
    purchase_price DECIMAL(19,2) NOT NULL,
    
    -- purchase_date: When this stock was first purchased
    -- TIMESTAMP NOT NULL: Date and time of purchase
    purchase_date TIMESTAMP NOT NULL,
    
    -- created_at: When this portfolio record was created in system
    -- TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP: Auto-set to current time
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- updated_at: When this portfolio record was last modified
    -- TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- UNIQUE constraint: A user can only own one record per stock
    -- Example: User 1 can have only one AAPL holding (not multiple records)
    UNIQUE KEY unique_user_symbol (user_id, symbol)
);

-- ============================================
-- CREATE TRADES TABLE
-- ============================================

-- The trades table is an audit log of all trading activity
-- Each row represents one buy or sell transaction
-- Example: User bought 10 AAPL = 1 row, User sold 5 AAPL = another row
CREATE TABLE trades (
    -- trade_id: Unique identifier for each trade
    -- INT NOT NULL AUTO_INCREMENT PRIMARY KEY: Auto-generated sequential ID
    trade_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- user_id: Links this trade to a specific user
    -- INT NOT NULL: User ID is required
    -- FOREIGN KEY: Value must exist in users table
    -- ON DELETE CASCADE: If user is deleted, delete their trade records too
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- symbol: Stock ticker that was traded
    -- VARCHAR(10): Can store symbols like AAPL, GOOGL, MSFT
    -- NOT NULL: Symbol is required
    symbol VARCHAR(10) NOT NULL,
    
    -- type: Whether this was a BUY or SELL
    -- VARCHAR(4): Stores either 'BUY' or 'SELL'
    -- NOT NULL: Trade type is required
    type VARCHAR(4) NOT NULL,
    
    -- quantity: Number of shares in this transaction
    -- INT NOT NULL: Quantity is required
    quantity INT NOT NULL,
    
    -- price: Price per share at time of trade
    -- DECIMAL(19,2): Can store currency values
    -- NOT NULL: Price is required
    price DECIMAL(19,2) NOT NULL,
    
    -- total_amount: Total value of transaction (quantity * price)
    -- DECIMAL(19,2): Total cost or proceeds
    -- NOT NULL: Total amount is required
    total_amount DECIMAL(19,2) NOT NULL,
    
    -- status: Transaction status
    -- VARCHAR(20): Can store values like 'PENDING', 'COMPLETED', 'FAILED'
    -- NOT NULL: Status is required
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    -- executed_at: When the trade was executed
    -- TIMESTAMP NOT NULL: Date and time of execution
    executed_at TIMESTAMP NOT NULL,
    
    -- created_at: When this record was created in the system
    -- TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP: Auto-set to current time
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- CREATE INDEXES (for faster queries)
-- ============================================

-- Index on user_id in portfolios table
-- Makes queries filtering by user faster
CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);

-- Index on symbol in portfolios table
-- Makes queries filtering by stock symbol faster
CREATE INDEX idx_portfolios_symbol ON portfolios(symbol);

-- Index on user_id in trades table
-- Makes queries filtering by user faster
CREATE INDEX idx_trades_user_id ON trades(user_id);

-- Index on symbol in trades table
-- Makes queries filtering by stock symbol faster
CREATE INDEX idx_trades_symbol ON trades(symbol);

-- Index on executed_at in trades table
-- Makes date range queries faster
CREATE INDEX idx_trades_executed_at ON trades(executed_at);

-- Index on type in trades table
-- Makes queries filtering by BUY/SELL faster
CREATE INDEX idx_trades_type ON trades(type);

-- ============================================
-- VERIFY TABLES WERE CREATED
-- ============================================

-- Show all tables in the database
-- SHOW TABLES;

-- Show structure of users table
-- DESCRIBE users;

-- Show structure of portfolios table
-- DESCRIBE portfolios;

-- Show structure of trades table
-- DESCRIBE trades;
