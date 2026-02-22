package com.kabu.admin.user.controller;

import com.kabu.admin.common.dto.LockPatchRequest;
import com.kabu.admin.common.dto.StatusPatchRequest;
import com.kabu.admin.user.dto.UserCreateRequest;
import com.kabu.admin.user.dto.UserListResponse;
import com.kabu.admin.user.dto.UserQueryRequest;
import com.kabu.admin.user.dto.UserResponse;
import com.kabu.admin.user.dto.UserUpdateRequest;
import com.kabu.admin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserListResponse listUsers(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer locked,
        @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        UserQueryRequest request = new UserQueryRequest(username, email, status, locked, tenantId, page, size);
        return userService.listUsers(request);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/status")
    public UserResponse updateUserStatus(@PathVariable Long id, @RequestBody StatusPatchRequest request) {
        return userService.updateUserStatus(id, request.status());
    }

    @PatchMapping("/{id}/lock")
    public UserResponse updateUserLock(@PathVariable Long id, @RequestBody LockPatchRequest request) {
        return userService.updateUserLock(id, request.locked());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
