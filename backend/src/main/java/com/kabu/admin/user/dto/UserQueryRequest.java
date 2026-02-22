package com.kabu.admin.user.dto;

public record UserQueryRequest(
    String username,
    String email,
    Integer status,
    Integer page,
    Integer size
) {
}
