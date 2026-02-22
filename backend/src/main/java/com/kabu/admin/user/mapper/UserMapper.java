package com.kabu.admin.user.mapper;

import com.kabu.admin.user.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    List<User> findByCriteria(
        @Param("username") String username,
        @Param("email") String email,
        @Param("status") Integer status,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("username") String username,
        @Param("email") String email,
        @Param("status") Integer status
    );

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);
}
