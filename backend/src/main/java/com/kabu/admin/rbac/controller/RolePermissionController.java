package com.kabu.admin.rbac.controller;

import com.kabu.admin.common.dto.IdBatchRequest;
import com.kabu.admin.rbac.dto.RolePermissionListResponse;
import com.kabu.admin.rbac.service.RolePermissionService;
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
@RequestMapping("/api/roles/{roleId}/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping
    public RolePermissionListResponse listRolePermissions(@PathVariable Long roleId) {
        return rolePermissionService.listRolePermissions(roleId);
    }

    @PutMapping
    public RolePermissionListResponse replaceRolePermissions(@PathVariable Long roleId, @RequestBody IdBatchRequest request) {
        return rolePermissionService.replaceRolePermissions(roleId, request.ids());
    }

    @PostMapping
    public RolePermissionListResponse addRolePermissions(@PathVariable Long roleId, @RequestBody IdBatchRequest request) {
        return rolePermissionService.addRolePermissions(roleId, request.ids());
    }

    @DeleteMapping
    public RolePermissionListResponse removeRolePermissions(@PathVariable Long roleId, @RequestBody IdBatchRequest request) {
        return rolePermissionService.removeRolePermissions(roleId, request.ids());
    }
}
