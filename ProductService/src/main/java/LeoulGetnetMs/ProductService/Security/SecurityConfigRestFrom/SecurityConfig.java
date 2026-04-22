package LeoulGetnetMs.ProductService.Security.SecurityConfigRestFrom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize in controllers
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()  // Health check public
                        .anyRequest().authenticated()  // All other endpoints need auth
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;}

    // Converts Keycloak roles to Spring Security authorities
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.get("roles") == null) {
                return List.of();}
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());}}}

//## ✅ **Simple Explanation of the 3 Main Objects:**
//        ## 1. **SecurityFilterChain** 🚪
//        **What it is:** The security guard at the entrance
//**What it does:**
//        - Decides which requests to let in
//- Tells Spring: "Require authentication for all endpoints except /actuator/health"
//        - Applies OAuth2 JWT validation to incoming requests (keycloak validation )
//**Analogy:** Building security who checks everyone's ID before entering
//extracts the jwt and passes the jwt but after validated it with keycloak.if invalid 401 is thrown.
//        ## 2. **JwtAuthenticationConverter** 🔄
//        **What it is:** The translator
//**What it does:**
//        - Takes the JWT token (Keycloak format)
//- Converts it to Spring Security's Authentication object
//        - Uses KeycloakRoleConverter to extract roles
//Authentication
//    │
//    ├── principal → User details (username, etc.)
//    ├── credentials → Password/token (usually cleared)
//    ├── authorities → Roles/permissions (ROLE_user, ROLE_admin)
//    └── authenticated → true/false
//
//**Analogy:** A translator who reads Keycloak's language and converts it to Spring Security's language
//        ## 3. **KeycloakRoleConverter**  👤
//        **What it is:** The role extractor
//**What it does:**
//        - Looks inside the JWT token
//- Finds the "realm_access" section
//- Extracts roles like `["user", "admin"]`
//        - Converts them to Spring format: `["ROLE_user", "ROLE_admin"]`
//        **Analogy:** Reading a passport to see what visas/permissions the person has
//        ## 🔄 **How They Work Together:**
//Request with JWT Token
//        │
//                ↓
//        ┌─────────────────────────────────────────┐
//                │ 1. SecurityFilterChain                  │
//        │    "Is this request allowed?"           │
//        │    → YES, let it through                │
//        └─────────────────────────────────────────┘
//        │
//        ↓
//        ┌─────────────────────────────────────────┐
//        │ 2. JwtAuthenticationConverter           │
//        │    "I need to convert this token"       │
//        │    → Calls KeycloakRoleConverter        │
//        └─────────────────────────────────────────┘
//        │
//        ↓
//        ┌─────────────────────────────────────────┐
//        │ 3. KeycloakRoleConverter                │
//        │    "Extract roles from token"           │
//        │    → "user" → "ROLE_user"               │
//        │    → "admin" → "ROLE_admin"             │
//        └─────────────────────────────────────────┘
//        │
//        ↓
//Spring Security knows: "User has roles: ROLE_user, ROLE_admin"
//        ## 📋 **Simple Summary:**
//        | Object | Job | Simple Analogy |
//        |--------|-----|----------------|
//        | **SecurityFilterChain** | Decide who gets in | Door security guard |
//        | **JwtAuthenticationConverter** | Translate token format | Translator |
//        | **KeycloakRoleConverter** | Extract roles from token | ID card reader |
//        **All work together to secure your API!** 🎯