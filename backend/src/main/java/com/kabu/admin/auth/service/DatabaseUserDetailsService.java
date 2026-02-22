package com.kabu.admin.auth.service;

import com.kabu.admin.auth.mapper.AuthUserMapper;
import com.kabu.admin.auth.model.AuthUser;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final AuthUserMapper authUserMapper;

    public DatabaseUserDetailsService(AuthUserMapper authUserMapper) {
        this.authUserMapper = authUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authUserMapper.findByUsername(username);
        if (user == null || user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new UsernameNotFoundException("用户密码未配置");
        }

        boolean enabled = Integer.valueOf(1).equals(user.getStatus());
        boolean accountNonLocked = !Integer.valueOf(1).equals(user.getAccountLocked());
        List<String> authorities = authUserMapper.findAuthoritiesByUsername(username);
        if (authorities == null) {
            authorities = List.of();
        }

        return User.withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(authorities.toArray(new String[0]))
            .disabled(!enabled)
            .accountLocked(!accountNonLocked)
            .build();
    }
}
