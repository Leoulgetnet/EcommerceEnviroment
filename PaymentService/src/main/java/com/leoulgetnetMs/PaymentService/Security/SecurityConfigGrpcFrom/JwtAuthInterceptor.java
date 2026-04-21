package com.leoulgetnetMs.PaymentService.Security.SecurityConfigGrpcFrom;

import io.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthInterceptor implements ServerInterceptor {

    @Autowired
    private JwtValidator jwtValidator;

    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        // Extract JWT from header
        String authHeader = headers.get(AUTHORIZATION_KEY);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid token"), headers);
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(7);

        // Validate with Keycloak
        try {
            jwtValidator.validateToken(token);
        } catch (Exception e) {
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid token: " + e.getMessage()), headers);
            return new ServerCall.Listener<>() {};
        }

        // Token valid, proceed
        return next.startCall(call, headers);
    }
}