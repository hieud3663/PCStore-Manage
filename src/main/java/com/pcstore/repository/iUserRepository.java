package com.pcstore.repository;

import com.pcstore.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(String username);
    List<User> findAll();
    boolean existsById(String username);
    void deleteById(String username);
    User authenticate(String username, String password);
    boolean updatePassword(String username, String newPassword);
    List<User> findByRole(int roleId);
    Optional<User> findByEmployeeId(String employeeId);
    int countByRole(int roleId);
}