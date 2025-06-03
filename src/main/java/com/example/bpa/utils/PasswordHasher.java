package com.example.bpa.utils;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordHasher {
    private static final PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 10);

    public static String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
