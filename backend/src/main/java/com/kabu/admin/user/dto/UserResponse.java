package com.kabu.admin.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String displayName,
    String email,
    Integer status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
