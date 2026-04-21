package com.example.EcommerceMs.ApiGateWay.LoginAuth;

import com.example.EcommerceMs.ApiGateWay.LoginAuth.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginResource {

    private final KeycloakAuthService keycloakAuthService;




    // ✅ 1. LOGIN - Already working
    @PostMapping("/login")
    public Mono<ResponseEntity<RestLoginResponse>> login(@RequestBody RestLoginRequest request) {
        return keycloakAuthService.getToken(request.username(), request.password())
                .map(this::toLoginResponse)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    // ✅ 2. REFRESH TOKEN - NEW!
    @PostMapping("/refresh")
    public Mono<ResponseEntity<RestLoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        return keycloakAuthService.refreshToken(request.refreshToken())
                .map(this::toLoginResponse)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    // ✅ 3. LOGOUT - NEW!
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody RefreshTokenRequest request) {
        return keycloakAuthService.logout(request.refreshToken())
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).build()));
    }

    private RestLoginResponse toLoginResponse(Map<String, Object> tokenResponse) {
        return new RestLoginResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                (String) tokenResponse.get("token_type"),
                ((Number) tokenResponse.get("expires_in")).longValue()
        );
    }
}