package com.kabu.admin.permission.controller;

import com.kabu.admin.common.dto.StatusPatchRequest;
import com.kabu.admin.permission.dto.PermissionCreateRequest;
import com.kabu.admin.permission.dto.PermissionListResponse;
import com.kabu.admin.permission.dto.PermissionQueryRequest;
import com.kabu.admin.permission.dto.PermissionResponse;
import com.kabu.admin.permission.dto.PermissionUpdateRequest;
import com.kabu.admin.permission.service.PermissionService;
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
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public PermissionListResponse listPermissions(
        @RequestParam(required = false) String permissionCode,
        @RequestParam(required = false) String permissionName,
        @RequestParam(required = false) String resourceType,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        PermissionQueryRequest request = new PermissionQueryRequest(
            permissionCode,
            permissionName,
            resourceType,
            status,
            page,
            size
        );
        return permissionService.listPermissions(request);
    }

    @GetMapping("/{id}")
    public PermissionResponse getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionResponse createPermission(@RequestBody PermissionCreateRequest request) {
        return permissionService.createPermission(request);
    }

    @PutMapping("/{id}")
    public PermissionResponse updatePermission(@PathVariable Long id, @RequestBody PermissionUpdateRequest request) {
        return permissionService.updatePermission(id, request);
    }

    @PatchMapping("/{id}/status")
    public PermissionResponse updatePermissionStatus(@PathVariable Long id, @RequestBody StatusPatchRequest request) {
        return permissionService.updatePermissionStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}
