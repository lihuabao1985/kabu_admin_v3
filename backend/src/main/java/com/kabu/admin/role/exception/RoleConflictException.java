package com.kabu.admin.role.exception;

public class RoleConflictException extends RuntimeException {

    public RoleConflictException(String roleCode) {
        super("角色编码已存在: " + roleCode);
    }
}
