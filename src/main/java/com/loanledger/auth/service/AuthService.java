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


}
