package com.retailshop.service;

import com.retailshop.dto.request.LoginRequest;
import com.retailshop.dto.request.RegisterRequest;
import com.retailshop.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
}
