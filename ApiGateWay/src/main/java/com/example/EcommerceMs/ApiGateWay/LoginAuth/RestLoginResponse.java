package com.example.EcommerceMs.ApiGateWay.LoginAuth;

public record RestLoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}