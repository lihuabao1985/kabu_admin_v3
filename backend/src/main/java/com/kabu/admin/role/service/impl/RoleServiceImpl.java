package com.kabu.admin.role.service.impl;

import com.kabu.admin.rbac.repository.RolePermissionRepository;
import com.kabu.admin.rbac.repository.UserRoleRepository;
import com.kabu.admin.role.dto.RoleCreateRequest;
import com.kabu.admin.role.dto.RoleListResponse;
import com.kabu.admin.role.dto.RoleQueryRequest;
import com.kabu.admin.role.dto.RoleResponse;
import com.kabu.admin.role.dto.RoleUpdateRequest;
import com.kabu.admin.role.exception.RoleConflictException;
import com.kabu.admin.role.exception.RoleNotFoundException;
import com.kabu.admin.role.exception.SystemRoleOperationException;
import com.kabu.admin.role.model.Role;
import com.kabu.admin.role.repository.RoleRepository;
import com.kabu.admin.role.service.RoleService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleServiceImpl(
        RoleRepository roleRepository,
        UserRoleRepository userRoleRepository,
        RolePermissionRepository rolePermissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleListResponse listRoles(RoleQueryRequest request) {
        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;

        String roleCode = normalizeText(request.roleCode());
        String roleName = normalizeText(request.roleName());
        Integer status = normalizeStatus(request.status(), true);

        List<RoleResponse> items = roleRepository.findByCriteria(roleCode, roleName, status, size, offset)
            .stream()
            .map(this::toResponse)
            .toList();
        long total = roleRepository.countByCriteria(roleCode, roleName, status);
        return new RoleListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        validateId(id);
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
        return toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        String roleCode = normalizeRoleCode(request.roleCode());
        String roleName = requireText(request.roleName(), "角色名称不能为空");

        if (roleRepository.findByRoleCode(roleCode).isPresent()) {
            throw new RoleConflictException(roleCode);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setDescription(normalizeNullableText(request.description()));
        role.setStatus(normalizeStatus(request.status(), false));
        role.setIsSystem(normalizeBooleanFlag(request.isSystem(), false));
        role.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        role.setCreatedAt(now);
        role.setUpdatedAt(now);

        int inserted = roleRepository.insert(role);
        if (inserted != 1 || role.getId() == null) {
            throw new IllegalStateException("创建角色失败");
        }
        return getRoleById(role.getId());
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        validateId(id);
        Role existing = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));

        String roleCode = normalizeRoleCode(request.roleCode());
        String roleName = requireText(request.roleName(), "角色名称不能为空");

        roleRepository.findByRoleCode(roleCode)
            .filter(role -> !role.getId().equals(id))
            .ifPresent(role -> {
                throw new RoleConflictException(roleCode);
            });

        if (isSystemRole(existing) && !existing.getRoleCode().equals(roleCode)) {
            throw new SystemRoleOperationException("系统角色编码不允许修改");
        }

        existing.setRoleCode(roleCode);
        existing.setRoleName(roleName);
        existing.setDescription(normalizeNullableText(request.description()));
        existing.setStatus(request.status() == null ? existing.getStatus() : normalizeStatus(request.status(), false));
        existing.setIsSystem(
            request.isSystem() == null
                ? existing.getIsSystem()
                : normalizeBooleanFlag(request.isSystem(), false)
        );
        existing.setSortOrder(request.sortOrder() == null ? existing.getSortOrder() : request.sortOrder());
        existing.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        int updated = roleRepository.update(existing);
        if (updated != 1) {
            throw new RoleNotFoundException(id);
        }
        return getRoleById(id);
    }

    @Override
    @Transactional
    public RoleResponse updateRoleStatus(Long id, Integer status) {
        validateId(id);
        Integer normalizedStatus = normalizeStatus(status, false);
        Role existing = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));

        if (isSystemRole(existing) && normalizedStatus == 0) {
            throw new SystemRoleOperationException("系统角色不允许停用");
        }

        int updated = roleRepository.updateStatus(id, normalizedStatus, LocalDateTime.now(ZoneOffset.UTC));
        if (updated != 1) {
            throw new RoleNotFoundException(id);
        }
        return getRoleById(id);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        validateId(id);
        Role existing = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
        if (isSystemRole(existing)) {
            throw new SystemRoleOperationException("系统角色不允许删除");
        }
        if (!userRoleRepository.findUserIdsByRoleId(id).isEmpty()) {
            throw new IllegalArgumentException("角色仍绑定用户，无法删除");
        }
        if (!rolePermissionRepository.findPermissionIdsByRoleId(id).isEmpty()) {
            throw new IllegalArgumentException("角色仍绑定权限，无法删除");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int deleted = roleRepository.softDelete(id, now, now);
        if (deleted != 1) {
            throw new RoleNotFoundException(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findExistingIds(List<Long> ids) {
        List<Long> normalizedIds = normalizeIdList(ids);
        if (normalizedIds.isEmpty()) {
            return List.of();
        }
        return roleRepository.findExistingIds(normalizedIds);
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
            role.getId(),
            role.getRoleCode(),
            role.getRoleName(),
            role.getDescription(),
            role.getStatus(),
            role.getIsSystem(),
            role.getSortOrder(),
            role.getCreatedAt(),
            role.getUpdatedAt()
        );
    }

    private boolean isSystemRole(Role role) {
        return Integer.valueOf(1).equals(role.getIsSystem());
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private String normalizeRoleCode(String value) {
        String code = requireText(value, "角色编码不能为空").toUpperCase();
        if (!code.startsWith("ROLE_")) {
            code = "ROLE_" + code;
        }
        return code;
    }

    private String requireText(String value, String message) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeNullableText(String value) {
        return normalizeText(value);
    }

    private Integer normalizeStatus(Integer status, boolean allowNull) {
        if (status == null) {
            return allowNull ? null : 1;
        }
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("状态必须为0或1");
        }
        return status;
    }

    private Integer normalizeBooleanFlag(Integer value, boolean allowNull) {
        if (value == null) {
            return allowNull ? null : 0;
        }
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("标志位必须为0或1");
        }
        return value;
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID必须为正整数");
        }
    }

    private List<Long> normalizeIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> uniqueIds = new TreeSet<>();
        for (Long id : ids) {
            if (id == null || id < 1) {
                throw new IllegalArgumentException("所有ID必须为正整数");
            }
            uniqueIds.add(id);
        }
        return new ArrayList<>(uniqueIds);
    }
}
