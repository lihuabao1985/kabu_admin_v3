package com.kabu.admin.rbac.repository;

import java.util.List;

public interface RolePermissionRepository {

    List<Long> findPermissionIdsByRoleId(Long roleId);

    int insertIgnoreBatch(Long roleId, List<Long> ids);

    int deleteByRoleIdAndPermissionIds(Long roleId, List<Long> ids);

    int deleteByRoleId(Long roleId);
}
