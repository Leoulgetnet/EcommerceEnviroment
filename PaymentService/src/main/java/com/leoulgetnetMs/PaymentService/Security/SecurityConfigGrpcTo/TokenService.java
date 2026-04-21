package com.leoulgetnetMs.PaymentService.Security.SecurityConfigGrpcTo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;@Service
public class TokenService {

    @Value("${spring.security.oauth2.client.registration.ecommerce-backend.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.ecommerce-backend.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.ecommerce-backend.token-uri}")
    private String tokenUri;

    private String serviceToken;
    private LocalDateTime tokenExpiry;

    public String getServiceToken() {
        if (serviceToken == null || tokenExpiry == null || tokenExpiry.isBefore(LocalDateTime.now())) {
            refreshToken();
        }
        return serviceToken;
    }

    private void refreshToken() {
        RestTemplate rest = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {

            Map<String, Object> response = rest.postForObject(tokenUri, request, Map.class);

            if (response != null && response.containsKey("access_token")) {
                serviceToken = (String) response.get("access_token");
                int expiresIn = (int) response.get("expires_in");
                tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn - 60);

            } else {
                throw new RuntimeException("No access token in response");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }
}