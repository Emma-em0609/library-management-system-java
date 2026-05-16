package com.example.library.controller;

import com.example.library.config.JwtService;
import com.example.library.model.AppUser;
import com.example.library.model.LoginRequest;
import com.example.library.model.RegisterRequest;
import com.example.library.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Авторизація", description = "Реєстрація та отримання JWT токену")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Увійти та отримати JWT токен")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Невірний логін або пароль"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        // Повертаємо токен і роль — фронтенд використає роль для показу/приховування кнопок
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", role,
                "username", request.getUsername()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Зареєструвати нового користувача (роль USER)")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Перевірка чи логін вже зайнятий
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Користувач з таким логіном вже існує"));
        }

        // Перевірка що поля не порожні
        if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Логін та пароль не можуть бути порожніми"));
        }

        // Мінімальна довжина пароля
        if (request.getPassword().length() < 4) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Пароль має бути не менше 4 символів"));
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_USER"); // Нові користувачі завжди отримують роль USER
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "Реєстрація успішна! Тепер увійдіть."));
    }
}