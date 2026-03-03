package org.magikarp.marketscanner.user;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<AppUser> authenticate(String username, String password) {
        String normalized = username == null ? "" : username.trim();
        String rawPassword = password == null ? "" : password;
        if (normalized.isEmpty() || rawPassword.isEmpty()) {
            return Optional.empty();
        }

        return appUserRepository.findByUsername(normalized)
                .filter(AppUser::isEnabled)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }
}
