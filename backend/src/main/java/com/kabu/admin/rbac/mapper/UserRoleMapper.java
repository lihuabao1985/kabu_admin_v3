package com.kabu.admin.rbac.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {

    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    int insertIgnoreBatch(
        @Param("userId") Long userId,
        @Param("ids") List<Long> ids,
        @Param("now") LocalDateTime now
    );

    int deleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    int deleteByUserId(@Param("userId") Long userId);
}
