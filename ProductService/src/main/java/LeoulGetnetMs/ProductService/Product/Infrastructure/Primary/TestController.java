package LeoulGetnetMs.ProductService.Product.Infrastructure.Primary;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {



    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint - no auth needed (but this is secured by filter!)";
    }

    @GetMapping("/secure")
    public String secureEndpoint(Principal principal) {
        return "Hello " + principal.getName() + "! You are authenticated!";
    }

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> info = new HashMap<>();
        info.put("name", auth.getName());
        info.put("roles", auth.getAuthorities());
        info.put("authenticated", auth.isAuthenticated());

        return info;
    }
}