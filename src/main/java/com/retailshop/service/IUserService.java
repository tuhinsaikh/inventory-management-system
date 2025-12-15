package com.retailshop.service;

import com.retailshop.entity.User;

import java.util.List;

public interface IUserService {
    User getUserById(Long userId);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getActiveUsers();
    User updateUser(Long userId, User user);
    void deleteUser(Long userId);
    void deactivateUser(Long userId);
    void activateUser(Long userId);
}
