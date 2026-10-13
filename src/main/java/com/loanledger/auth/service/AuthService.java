package com.loanledger.auth.service;

import com.loanledger.auth.dto.AuthResponse;
import com.loanledger.auth.dto.LoginRequest;
import com.loanledger.auth.dto.RegisterRequest;
import com.loanledger.auth.security.JwtService;
import com.loanledger.common.exception.BusinessException;
import com.loanledger.user.dto.UserResponse;
import com.loanledger.user.entity.Role;
import com.loanledger.user.entity.User;
import com.loanledger.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already taken", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                Role.ROLE_CUSTOMER
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return AuthResponse.of(token, UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!user.isEnabled()) {
            throw new BusinessException("Account is disabled", HttpStatus.FORBIDDEN);
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return AuthResponse.of(token, UserResponse.from(user));
    }
}
