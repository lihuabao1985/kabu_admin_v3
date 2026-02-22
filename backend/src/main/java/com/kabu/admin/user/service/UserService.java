package com.kabu.admin.user.service;

import com.kabu.admin.user.dto.UserCreateRequest;
import com.kabu.admin.user.dto.UserListResponse;
import com.kabu.admin.user.dto.UserQueryRequest;
import com.kabu.admin.user.dto.UserResponse;
import com.kabu.admin.user.dto.UserUpdateRequest;

public interface UserService {

    UserListResponse listUsers(UserQueryRequest request);

    UserResponse getUserById(Long id);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse updateUserStatus(Long id, Integer status);

    UserResponse updateUserLock(Long id, Integer locked);

    void deleteUser(Long id);
}
