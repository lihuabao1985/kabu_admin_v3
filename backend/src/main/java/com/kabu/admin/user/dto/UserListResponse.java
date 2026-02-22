package com.kabu.admin.user.dto;

import java.util.List;

public record UserListResponse(
    List<UserResponse> items,
    long total,
    int page,
    int size
) {
}
