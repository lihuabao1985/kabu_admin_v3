package com.kabu.admin.role.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kabu.admin.rbac.repository.RolePermissionRepository;
import com.kabu.admin.rbac.repository.UserRoleRepository;
import com.kabu.admin.role.dto.RoleCreateRequest;
import com.kabu.admin.role.dto.RoleUpdateRequest;
import com.kabu.admin.role.exception.RoleConflictException;
import com.kabu.admin.role.exception.SystemRoleOperationException;
import com.kabu.admin.role.model.Role;
import com.kabu.admin.role.repository.RoleRepository;
import com.kabu.admin.role.service.impl.RoleServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void createRoleShouldNormalizeRoleCodeAndPersist() {
        RoleCreateRequest request = new RoleCreateRequest("admin", "Admin", "system admin", null, null, null);
        Role stored = buildRole(1L, "ROLE_ADMIN", "Admin", 1, 0);

        when(roleRepository.findByRoleCode("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.insert(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(1L);
            return 1;
        });
        when(roleRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = roleService.createRole(request);

        assertEquals("ROLE_ADMIN", response.roleCode());
        assertEquals("Admin", response.roleName());
        assertEquals(1, response.status());
    }

    @Test
    void createRoleShouldFailWhenRoleCodeExists() {
        RoleCreateRequest request = new RoleCreateRequest("ROLE_ADMIN", "Admin", null, null, null, null);
        when(roleRepository.findByRoleCode("ROLE_ADMIN")).thenReturn(Optional.of(buildRole(2L, "ROLE_ADMIN", "Admin", 1, 1)));

        assertThrows(RoleConflictException.class, () -> roleService.createRole(request));
    }

    @Test
    void updateRoleShouldFailWhenRoleCodeAlreadyExists() {
        Role existing = buildRole(1L, "ROLE_OPERATOR", "Operator", 1, 0);
        Role another = buildRole(2L, "ROLE_ADMIN", "Admin", 1, 1);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByRoleCode("ROLE_ADMIN")).thenReturn(Optional.of(another));

        assertThrows(
            RoleConflictException.class,
            () -> roleService.updateRole(1L, new RoleUpdateRequest("ROLE_ADMIN", "Operator", null, 1, 0, 0))
        );
    }

    @Test
    void deleteRoleShouldRejectSystemRole() {
        Role systemRole = buildRole(1L, "ROLE_ADMIN", "Admin", 1, 1);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(systemRole));

        assertThrows(SystemRoleOperationException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void deleteRoleShouldRejectWhenStillAssignedToUsers() {
        Role role = buildRole(2L, "ROLE_OPERATOR", "Operator", 1, 0);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(userRoleRepository.findUserIdsByRoleId(2L)).thenReturn(List.of(100L));

        assertThrows(IllegalArgumentException.class, () -> roleService.deleteRole(2L));
    }

    private Role buildRole(Long id, String roleCode, String roleName, Integer status, Integer isSystem) {
        Role role = new Role();
        role.setId(id);
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setStatus(status);
        role.setIsSystem(isSystem);
        role.setSortOrder(0);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return role;
    }
}
