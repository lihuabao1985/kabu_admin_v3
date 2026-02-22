package com.kabu.admin.role.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(Long id) {
        super("角色不存在: " + id);
    }
}
