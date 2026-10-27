package com.loanledger.user.dto;

import com.loanledger.user.entity.Role;
import com.loanledger.user.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        Role role,
        boolean enabled
)
