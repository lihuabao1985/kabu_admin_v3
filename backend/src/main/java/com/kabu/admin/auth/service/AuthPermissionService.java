package com.kabu.admin.auth.service;

import com.kabu.admin.auth.mapper.AuthUserMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthPermissionService {

    private final AuthUserMapper authUserMapper;

    public AuthPermissionService(AuthUserMapper authUserMapper) {
        this.authUserMapper = authUserMapper;
    }

    public List<String> getPermissionsByUsername(String username) {
        List<String> authorities = authUserMapper.findAuthoritiesByUsername(username);
        if (authorities == null) {
            return List.of();
        }
        return authorities;
    }
}
