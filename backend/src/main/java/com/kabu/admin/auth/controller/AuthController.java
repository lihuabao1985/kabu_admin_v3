package com.kabu.admin.auth.controller;

import com.kabu.admin.auth.dto.AuthPermissionResponse;
import com.kabu.admin.auth.service.AuthPermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthPermissionService authPermissionService;

    public AuthController(AuthPermissionService authPermissionService) {
        this.authPermissionService = authPermissionService;
    }

    @GetMapping("/me/permissions")
    public AuthPermissionResponse getCurrentUserPermissions(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("认证信息不能为空");
        }
        return new AuthPermissionResponse(authPermissionService.getPermissionsByUsername(authentication.getName()));
    }
}
