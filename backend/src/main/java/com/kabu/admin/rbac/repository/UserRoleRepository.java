package com.kabu.admin.rbac.repository;

import java.util.List;

public interface UserRoleRepository {

    List<Long> findRoleIdsByUserId(Long userId);

    List<Long> findUserIdsByRoleId(Long roleId);

    int insertIgnoreBatch(Long userId, List<Long> ids);

    int deleteByUserIdAndRoleIds(Long userId, List<Long> ids);

    int deleteByUserId(Long userId);
}
