package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.entity.User;
import com.retailshop.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> response = ApiResponse.<List<User>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("User deactivated successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("User activated successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
