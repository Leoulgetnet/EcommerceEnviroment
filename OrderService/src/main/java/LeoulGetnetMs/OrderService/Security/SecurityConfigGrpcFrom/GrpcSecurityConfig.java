package LeoulGetnetMs.OrderService.Security.SecurityConfigGrpcFrom;

import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcSecurityConfig {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurer(JwtAuthInterceptor jwtAuthInterceptor) {
        return registry -> registry.add(jwtAuthInterceptor);  // ← Use .add(), not .addServerInterceptor()
    }
}