package com.kabu.admin.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kabu.admin.permission.service.PermissionService;
import com.kabu.admin.rbac.repository.RolePermissionRepository;
import com.kabu.admin.rbac.service.impl.RolePermissionServiceImpl;
import com.kabu.admin.role.dto.RoleResponse;
import com.kabu.admin.role.service.RoleService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

    @Mock
    private RoleService roleService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    void listRolePermissionsShouldReturnPermissionIds() {
        when(roleService.getRoleById(1L)).thenReturn(buildRoleResponse(1L));
        when(rolePermissionRepository.findPermissionIdsByRoleId(1L)).thenReturn(List.of(11L, 12L));

        var response = rolePermissionService.listRolePermissions(1L);

        assertEquals(List.of(11L, 12L), response.permissionIds());
    }

    @Test
    void addRolePermissionsShouldFailWhenPermissionDisabled() {
        when(roleService.getRoleById(1L)).thenReturn(buildRoleResponse(1L));
        when(permissionService.findExistingEnabledIds(List.of(11L, 12L))).thenReturn(List.of(11L));

        assertThrows(
            IllegalArgumentException.class,
            () -> rolePermissionService.addRolePermissions(1L, List.of(11L, 12L))
        );
    }

    private RoleResponse buildRoleResponse(Long id) {
        return new RoleResponse(
            id,
            "ROLE_ADMIN",
            "Admin",
            null,
            1,
            1,
            0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
