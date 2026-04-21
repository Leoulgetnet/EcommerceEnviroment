package com.example.EcommerceMs.ApiGateWay.LoginAuth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class KeycloakAuthService {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.ecommerce-backend.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.ecommerce-backend.logout-uri}")
    private String logoutUri;

    @Value("${spring.security.oauth2.client.registration.ecommerce-backend.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.ecommerce-backend.client-secret}")
    private String clientSecret;

    public KeycloakAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // ✅ 1. LOGIN - Get both tokens
    public Mono<Map> getToken(String username, String password) {
        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", username)
                        .with("password", password)
                        .with("grant_type", "password"))
                .retrieve()
                .bodyToMono(Map.class);
    }

    // ✅ 2. REFRESH TOKEN - Get NEW access token using refresh token
    public Mono<Map> refreshToken(String refreshToken) {
        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", refreshToken)
                        .with("grant_type", "refresh_token"))
                .retrieve()
                .bodyToMono(Map.class);
    }

    // ✅ 3. LOGOUT - Invalidate refresh token
    public Mono<Void> logout(String refreshToken) {
        return webClient.post()
                .uri(logoutUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", refreshToken))
                .retrieve()
                .bodyToMono(Void.class);
    }
}