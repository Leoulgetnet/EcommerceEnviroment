package LeoulGetnetMs.ProductService.Security.SecurityConfigGrpcFrom;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {
        this.jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
    }

    public Jwt validateToken(String token) {
        try {
            /*Accepts only the token without bearer
            * if Decoding is successful , it means the jwt is valid else it will throw
            * JwtException imply that it is expired , invalid or something else.*/
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT to                                                              ken");
        }
    }
}