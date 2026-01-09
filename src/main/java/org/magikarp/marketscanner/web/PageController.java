package org.magikarp.marketscanner.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.magikarp.marketscanner.user.AppUser;
import org.magikarp.marketscanner.user.AppUserService;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    private final Environment environment;
    private final AppUserService appUserService;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public PageController(Environment environment, AppUserService appUserService) {
        this.environment = environment;
        this.appUserService = appUserService;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        // Root is permitAll; route based on real Spring Security auth state
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }
        model.addAttribute("oauthEnabled", isOauthEnabled());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        return appUserService.authenticate(username, password)
                .map(user -> signIn(user, request, response))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
                    return "redirect:/login";
                });
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        // /home is authenticated() in SecurityConfig, so normally you won’t reach here unauthenticated
        String username = (authentication != null) ? authentication.getName() : null;
        model.addAttribute("username", username);
        return "home";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }

    private boolean isOauthEnabled() {
        String clientId = environment.getProperty(
                "spring.security.oauth2.client.registration.google.client-id"
        );
        return clientId != null && !clientId.isBlank();
    }

    private String signIn(AppUser user, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return "redirect:/home";
    }
}
