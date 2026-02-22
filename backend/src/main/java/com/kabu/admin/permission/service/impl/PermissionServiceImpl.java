package com.kabu.admin.permission.service.impl;

import com.kabu.admin.permission.dto.PermissionCreateRequest;
import com.kabu.admin.permission.dto.PermissionListResponse;
import com.kabu.admin.permission.dto.PermissionQueryRequest;
import com.kabu.admin.permission.dto.PermissionResponse;
import com.kabu.admin.permission.dto.PermissionUpdateRequest;
import com.kabu.admin.permission.exception.PermissionConflictException;
import com.kabu.admin.permission.exception.PermissionNotFoundException;
import com.kabu.admin.permission.model.Permission;
import com.kabu.admin.permission.repository.PermissionRepository;
import com.kabu.admin.permission.service.PermissionService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final String PERMISSION_CODE_PATTERN = "^[A-Z0-9_]+:[A-Z0-9_]+:[A-Z0-9_]+$";
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionListResponse listPermissions(PermissionQueryRequest request) {
        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;

        String permissionCode = normalizeText(request.permissionCode());
        String permissionName = normalizeText(request.permissionName());
        String resourceType = normalizeResourceType(request.resourceType(), true);
        Integer status = normalizeStatus(request.status(), true);

        List<PermissionResponse> items = permissionRepository
            .findByCriteria(permissionCode, permissionName, resourceType, status, size, offset)
            .stream()
            .map(this::toResponse)
            .toList();
        long total = permissionRepository.countByCriteria(permissionCode, permissionName, resourceType, status);
        return new PermissionListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long id) {
        validateId(id);
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new PermissionNotFoundException(id));
        return toResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionCreateRequest request) {
        String permissionCode = normalizePermissionCode(request.permissionCode());
        String permissionName = requireText(request.permissionName(), "权限名称不能为空");

        if (permissionRepository.findByPermissionCode(permissionCode).isPresent()) {
            throw new PermissionConflictException(permissionCode);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Permission permission = new Permission();
        permission.setPermissionCode(permissionCode);
        permission.setPermissionName(permissionName);
        permission.setDescription(normalizeNullableText(request.description()));
        permission.setStatus(normalizeStatus(request.status(), false));
        permission.setResourceType(normalizeResourceType(request.resourceType(), false));
        permission.setResource(requireText(request.resource(), "资源标识不能为空"));
        permission.setHttpMethod(normalizeNullableText(request.httpMethod()));
        permission.setAction(normalizeNullableText(request.action()));
        permission.setPermissionGroup(normalizeNullableText(request.permissionGroup()));
        permission.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        permission.setUiMenuKey(normalizeNullableText(request.uiMenuKey()));
        permission.setUiRoute(normalizeNullableText(request.uiRoute()));
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);

        int inserted = permissionRepository.insert(permission);
        if (inserted != 1 || permission.getId() == null) {
            throw new IllegalStateException("创建权限失败");
        }
        return getPermissionById(permission.getId());
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionUpdateRequest request) {
        validateId(id);
        Permission existing = permissionRepository.findById(id)
            .orElseThrow(() -> new PermissionNotFoundException(id));

        String permissionCode = normalizePermissionCode(request.permissionCode());
        String permissionName = requireText(request.permissionName(), "权限名称不能为空");

        permissionRepository.findByPermissionCode(permissionCode)
            .filter(item -> !item.getId().equals(id))
            .ifPresent(item -> {
                throw new PermissionConflictException(permissionCode);
            });

        existing.setPermissionCode(permissionCode);
        existing.setPermissionName(permissionName);
        existing.setDescription(normalizeNullableText(request.description()));
        existing.setStatus(request.status() == null ? existing.getStatus() : normalizeStatus(request.status(), false));
        existing.setResourceType(normalizeResourceType(request.resourceType(), false));
        existing.setResource(requireText(request.resource(), "资源标识不能为空"));
        existing.setHttpMethod(normalizeNullableText(request.httpMethod()));
        existing.setAction(normalizeNullableText(request.action()));
        existing.setPermissionGroup(normalizeNullableText(request.permissionGroup()));
        existing.setSortOrder(request.sortOrder() == null ? existing.getSortOrder() : request.sortOrder());
        existing.setUiMenuKey(normalizeNullableText(request.uiMenuKey()));
        existing.setUiRoute(normalizeNullableText(request.uiRoute()));
        existing.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        int updated = permissionRepository.update(existing);
        if (updated != 1) {
            throw new PermissionNotFoundException(id);
        }
        return getPermissionById(id);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermissionStatus(Long id, Integer status) {
        validateId(id);
        Integer normalizedStatus = normalizeStatus(status, false);
        permissionRepository.findById(id)
            .orElseThrow(() -> new PermissionNotFoundException(id));

        int updated = permissionRepository.updateStatus(id, normalizedStatus, LocalDateTime.now(ZoneOffset.UTC));
        if (updated != 1) {
            throw new PermissionNotFoundException(id);
        }
        return getPermissionById(id);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        validateId(id);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int deleted = permissionRepository.softDelete(id, now, now);
        if (deleted != 1) {
            throw new PermissionNotFoundException(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findExistingEnabledIds(List<Long> ids) {
        List<Long> normalizedIds = normalizeIdList(ids);
        if (normalizedIds.isEmpty()) {
            return List.of();
        }
        return permissionRepository.findExistingEnabledIds(normalizedIds);
    }

    private PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
            permission.getId(),
            permission.getPermissionCode(),
            permission.getPermissionName(),
            permission.getDescription(),
            permission.getStatus(),
            permission.getResourceType(),
            permission.getResource(),
            permission.getHttpMethod(),
            permission.getAction(),
            permission.getPermissionGroup(),
            permission.getSortOrder(),
            permission.getUiMenuKey(),
            permission.getUiRoute(),
            permission.getCreatedAt(),
            permission.getUpdatedAt()
        );
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

    private String normalizePermissionCode(String value) {
        String code = requireText(value, "权限编码不能为空").toUpperCase();
        if (!code.matches(PERMISSION_CODE_PATTERN)) {
            throw new IllegalArgumentException(
                "权限编码格式必须为 DOMAIN:RESOURCE:ACTION，仅允许 A-Z/0-9/_"
            );
        }
        return code;
    }

    private String normalizeResourceType(String value, boolean allowNull) {
        if (value == null) {
            if (allowNull) {
                return null;
            }
            return "API";
        }
        String normalized = requireText(value, "资源类型不能为空").toUpperCase();
        if (!"API".equals(normalized) && !"UI".equals(normalized) && !"DATA".equals(normalized)) {
            throw new IllegalArgumentException("资源类型必须是 API、UI 或 DATA");
        }
        return normalized;
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
