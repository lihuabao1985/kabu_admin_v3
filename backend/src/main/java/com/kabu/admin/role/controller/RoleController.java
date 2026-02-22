package com.kabu.admin.role.controller;

import com.kabu.admin.common.dto.StatusPatchRequest;
import com.kabu.admin.role.dto.RoleCreateRequest;
import com.kabu.admin.role.dto.RoleListResponse;
import com.kabu.admin.role.dto.RoleQueryRequest;
import com.kabu.admin.role.dto.RoleResponse;
import com.kabu.admin.role.dto.RoleUpdateRequest;
import com.kabu.admin.role.service.RoleService;
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
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public RoleListResponse listRoles(
        @RequestParam(required = false) String roleCode,
        @RequestParam(required = false) String roleName,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        RoleQueryRequest request = new RoleQueryRequest(roleCode, roleName, status, page, size);
        return roleService.listRoles(request);
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse createRole(@RequestBody RoleCreateRequest request) {
        return roleService.createRole(request);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return roleService.updateRole(id, request);
    }

    @PatchMapping("/{id}/status")
    public RoleResponse updateRoleStatus(@PathVariable Long id, @RequestBody StatusPatchRequest request) {
        return roleService.updateRoleStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
