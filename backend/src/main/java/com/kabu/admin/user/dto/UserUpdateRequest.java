package com.kabu.admin.user.dto;

public record UserUpdateRequest(
    String username,
    String displayName,
    String email,
    String phone,
    String password,
    Integer status
) {
}
