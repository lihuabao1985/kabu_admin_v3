package com.kabu.admin.rbac.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {

    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    int insertIgnoreBatch(
        @Param("roleId") Long roleId,
        @Param("ids") List<Long> ids,
        @Param("now") LocalDateTime now
    );

    int deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("ids") List<Long> ids);

    int deleteByRoleId(@Param("roleId") Long roleId);
}
