package com.gamified.stocksimulator.repository;

import com.gamified.stocksimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository Interface - Database Access Layer for User Entity
 * 
 * This repository extends JpaRepository which provides CRUD (Create, Read, Update, Delete) operations.
 * It automatically creates the implementation at runtime using Spring Data JPA.
 * 
 * Key Points:
 *   - JpaRepository<User, Long> - Generic parameters: Entity class, Primary key type
 *   - Custom methods defined here will be implemented automatically by Spring Data
 *   - Query methods use naming conventions: findBy[FieldName], findAllBy[FieldName], etc.
 * 
 * Available Methods (inherited from JpaRepository):
 *   - save(User) - Insert or update a user
 *   - findById(Long) - Get user by ID
 *   - findAll() - Get all users
 *   - delete(User) - Delete a user
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username
     * 
     * Spring Data JPA generates this query automatically based on the method name.
     * It searches the 'users' table for a row where username matches the parameter.
     * 
     * @param username The username to search for
     * @return Optional<User> - Contains the user if found, empty if not found
     * 
     * SQL equivalent: SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email address
     * 
     * Spring Data JPA generates this query automatically.
     * It searches the 'users' table for a row where email matches the parameter.
     * 
     * @param email The email address to search for
     * @return Optional<User> - Contains the user if found, empty if not found
     * 
     * SQL equivalent: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists by username
     * 
     * This method is efficient for checking existence without loading the entire user object.
     * Returns true if a user with the given username exists, false otherwise.
     * 
     * @param username The username to check
     * @return true if user exists, false if not
     * 
     * SQL equivalent: SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists by email
     * 
     * This method is efficient for checking existence without loading the entire user object.
     * Returns true if a user with the given email exists, false otherwise.
     * 
     * @param email The email address to check
     * @return true if user exists, false if not
     * 
     * SQL equivalent: SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
     */
    boolean existsByEmail(String email);
}
