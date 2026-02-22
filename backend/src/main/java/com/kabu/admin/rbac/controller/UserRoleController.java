package com.kabu.admin.rbac.controller;

import com.kabu.admin.common.dto.IdBatchRequest;
import com.kabu.admin.rbac.dto.UserRoleListResponse;
import com.kabu.admin.rbac.service.UserRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@PreAuthorize("hasRole('ADMIN')")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public UserRoleListResponse listUserRoles(@PathVariable Long userId) {
        return userRoleService.listUserRoles(userId);
    }

    @PutMapping
    public UserRoleListResponse replaceUserRoles(@PathVariable Long userId, @RequestBody IdBatchRequest request) {
        return userRoleService.replaceUserRoles(userId, request.ids());
    }

    @PostMapping
    public UserRoleListResponse addUserRoles(@PathVariable Long userId, @RequestBody IdBatchRequest request) {
        return userRoleService.addUserRoles(userId, request.ids());
    }

    @DeleteMapping
    public UserRoleListResponse removeUserRoles(@PathVariable Long userId, @RequestBody IdBatchRequest request) {
        return userRoleService.removeUserRoles(userId, request.ids());
    }
}
