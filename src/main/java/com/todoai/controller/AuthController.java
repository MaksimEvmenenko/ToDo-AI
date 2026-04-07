package com.todoai.controller;

import com.todoai.dto.AuthResponse;
import com.todoai.dto.LoginRequest;
import com.todoai.dto.SignUpRequest;
import com.todoai.model.User;
import com.todoai.repository.UserRepository;
import com.todoai.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());
            
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            
            AuthResponse response = AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .userId(user != null ? user.getId() : null)
                    .username(request.getUsername())
                    .build();

            log.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Login failed for user: {}", request.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().build());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AuthResponse.builder().build());
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AuthResponse.builder().build());
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .enabled(true)
                    .accountNonLocked(true)
                    .build();

            User savedUser = userRepository.save(user);

            String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

            AuthResponse response = AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .build();

            log.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            log.error("Signup failed for user: {}", request.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder().build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.builder().build());
            }

            String token = authHeader.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.builder().build());
            }

            String username = jwtTokenProvider.getUsernameFromJwt(token);
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

            User user = userRepository.findByUsername(username).orElse(null);

            AuthResponse response = AuthResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .type("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .userId(user != null ? user.getId() : null)
                    .username(username)
                    .build();

            log.info("Token refreshed for user: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Token refresh failed", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().build());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok("User info endpoint");
    }
}

