package com.kabu.admin.role.repository;

import com.kabu.admin.role.model.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    List<Role> findByCriteria(String roleCode, String roleName, Integer status, int limit, int offset);

    long countByCriteria(String roleCode, String roleName, Integer status);

    Optional<Role> findById(Long id);

    Optional<Role> findByRoleCode(String roleCode);

    int insert(Role role);

    int update(Role role);

    int updateStatus(Long id, Integer status, LocalDateTime updatedAt);

    int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt);

    List<Long> findExistingIds(List<Long> ids);
}
