package org.magikarp.marketscanner.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppUserSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserSeeder(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        appUserRepository.findByUsername("demo").orElseGet(() -> {
            AppUser user = new AppUser();
            user.setUsername("demo");
            user.setPasswordHash(passwordEncoder.encode("demo"));
            user.setEnabled(true);
            return appUserRepository.save(user);
        });
    }
}
