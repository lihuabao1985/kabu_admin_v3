package com.kabu.admin.role.mapper;

import com.kabu.admin.role.model.Role;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {

    List<Role> findByCriteria(
        @Param("roleCode") String roleCode,
        @Param("roleName") String roleName,
        @Param("status") Integer status,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("roleCode") String roleCode,
        @Param("roleName") String roleName,
        @Param("status") Integer status
    );

    Role findById(@Param("id") Long id);

    Role findByRoleCode(@Param("roleCode") String roleCode);

    int insert(Role role);

    int update(Role role);

    int updateStatus(
        @Param("id") Long id,
        @Param("status") Integer status,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    int softDelete(
        @Param("id") Long id,
        @Param("deletedAt") LocalDateTime deletedAt,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    List<Long> findExistingIds(@Param("ids") List<Long> ids);
}
