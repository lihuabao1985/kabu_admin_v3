package com.kabu.admin.permission.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kabu.admin.permission.dto.PermissionCreateRequest;
import com.kabu.admin.permission.dto.PermissionUpdateRequest;
import com.kabu.admin.permission.exception.PermissionConflictException;
import com.kabu.admin.permission.model.Permission;
import com.kabu.admin.permission.repository.PermissionRepository;
import com.kabu.admin.permission.service.impl.PermissionServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void createPermissionShouldPersist() {
        PermissionCreateRequest request = new PermissionCreateRequest(
            "stock:price:read",
            "Stock Price Read",
            "Read stock prices",
            null,
            "API",
            "/api/stocks",
            "GET",
            "READ",
            "STOCK",
            1,
            "stocks",
            "/stocks"
        );
        Permission stored = buildPermission(1L, "STOCK:PRICE:READ");
        stored.setPermissionName("Stock Price Read");

        when(permissionRepository.findByPermissionCode("STOCK:PRICE:READ")).thenReturn(Optional.empty());
        when(permissionRepository.insert(any(Permission.class))).thenAnswer(invocation -> {
            Permission permission = invocation.getArgument(0);
            permission.setId(1L);
            return 1;
        });
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = permissionService.createPermission(request);

        assertEquals("STOCK:PRICE:READ", response.permissionCode());
        assertEquals("Stock Price Read", response.permissionName());
        assertEquals(1, response.status());
    }

    @Test
    void createPermissionShouldFailWhenCodeExists() {
        PermissionCreateRequest request = new PermissionCreateRequest(
            "STOCK:PRICE:READ",
            "Read",
            null,
            1,
            "API",
            "/api/stocks",
            "GET",
            "READ",
            null,
            null,
            null,
            null
        );
        when(permissionRepository.findByPermissionCode("STOCK:PRICE:READ"))
            .thenReturn(Optional.of(buildPermission(2L, "STOCK:PRICE:READ")));

        assertThrows(PermissionConflictException.class, () -> permissionService.createPermission(request));
    }

    @Test
    void createPermissionShouldFailWhenCodeFormatInvalid() {
        PermissionCreateRequest request = new PermissionCreateRequest(
            "INVALID-CODE",
            "Read",
            null,
            1,
            "API",
            "/api/stocks",
            "GET",
            "READ",
            null,
            null,
            null,
            null
        );

        assertThrows(IllegalArgumentException.class, () -> permissionService.createPermission(request));
    }

    @Test
    void updatePermissionShouldFailWhenCodeConflict() {
        Permission existing = buildPermission(1L, "STOCK:PRICE:READ");
        Permission conflict = buildPermission(2L, "SECURITY:ROLE:WRITE");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(permissionRepository.findByPermissionCode("SECURITY:ROLE:WRITE")).thenReturn(Optional.of(conflict));

        assertThrows(
            PermissionConflictException.class,
            () -> permissionService.updatePermission(
                1L,
                new PermissionUpdateRequest(
                    "SECURITY:ROLE:WRITE",
                    "Write roles",
                    null,
                    1,
                    "API",
                    "/api/roles",
                    "POST",
                    "WRITE",
                    "SECURITY",
                    0,
                    null,
                    null
                )
            )
        );
    }

    private Permission buildPermission(Long id, String code) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setPermissionCode(code);
        permission.setPermissionName("Name");
        permission.setStatus(1);
        permission.setResourceType("API");
        permission.setResource("/api/resource");
        permission.setSortOrder(0);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        return permission;
    }
}
