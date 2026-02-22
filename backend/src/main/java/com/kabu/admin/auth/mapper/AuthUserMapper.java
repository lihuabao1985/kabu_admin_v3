package com.kabu.admin.auth.mapper;

import com.kabu.admin.auth.model.AuthUser;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthUserMapper {

    AuthUser findByUsername(@Param("username") String username);

    List<String> findAuthoritiesByUsername(@Param("username") String username);
}
