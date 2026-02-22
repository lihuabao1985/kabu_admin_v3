package com.kabu.admin.rbac.service.impl;

import com.kabu.admin.rbac.dto.UserRoleListResponse;
import com.kabu.admin.rbac.repository.UserRoleRepository;
import com.kabu.admin.rbac.service.UserRoleService;
import com.kabu.admin.role.service.RoleService;
import com.kabu.admin.user.exception.UserNotFoundException;
import com.kabu.admin.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(
        UserRepository userRepository,
        RoleService roleService,
        UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleListResponse listUserRoles(Long userId) {
        validateUserExists(userId);
        return new UserRoleListResponse(userRoleRepository.findRoleIdsByUserId(userId));
    }

    @Override
    @Transactional
    public UserRoleListResponse replaceUserRoles(Long userId, List<Long> roleIds) {
        validateUserExists(userId);
        List<Long> normalizedRoleIds = normalizeIdList(roleIds);
        validateRoleIds(normalizedRoleIds);
        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.insertIgnoreBatch(userId, normalizedRoleIds);
        return listUserRoles(userId);
    }

    @Override
    @Transactional
    public UserRoleListResponse addUserRoles(Long userId, List<Long> roleIds) {
        validateUserExists(userId);
        List<Long> normalizedRoleIds = normalizeIdList(roleIds);
        validateRoleIds(normalizedRoleIds);
        userRoleRepository.insertIgnoreBatch(userId, normalizedRoleIds);
        return listUserRoles(userId);
    }

    @Override
    @Transactional
    public UserRoleListResponse removeUserRoles(Long userId, List<Long> roleIds) {
        validateUserExists(userId);
        List<Long> normalizedRoleIds = normalizeIdList(roleIds);
        userRoleRepository.deleteByUserIdAndRoleIds(userId, normalizedRoleIds);
        return listUserRoles(userId);
    }

    private void validateUserExists(Long userId) {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("用户ID必须为正整数");
        }
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateRoleIds(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        List<Long> existingIds = roleService.findExistingIds(roleIds);
        if (existingIds.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色ID不存在");
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
