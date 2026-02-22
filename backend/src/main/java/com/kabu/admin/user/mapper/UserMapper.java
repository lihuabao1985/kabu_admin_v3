package com.kabu.admin.user.mapper;

import com.kabu.admin.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    List<User> findByCriteria(
        @Param("username") String username,
        @Param("email") String email,
        @Param("status") Integer status,
        @Param("locked") Integer locked,
        @Param("tenantId") String tenantId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("username") String username,
        @Param("email") String email,
        @Param("status") Integer status,
        @Param("locked") Integer locked,
        @Param("tenantId") String tenantId
    );

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

    User findByPhone(@Param("phone") String phone);

    int insert(User user);

    int update(User user);

    int updateStatus(
        @Param("id") Long id,
        @Param("status") Integer status,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    int updateLock(
        @Param("id") Long id,
        @Param("locked") Integer locked,
        @Param("lockedAt") LocalDateTime lockedAt,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    int softDelete(
        @Param("id") Long id,
        @Param("deletedAt") LocalDateTime deletedAt,
        @Param("updatedAt") LocalDateTime updatedAt
    );
}
