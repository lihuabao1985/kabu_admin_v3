package com.kabu.admin.permission.exception;

public class PermissionNotFoundException extends RuntimeException {

    public PermissionNotFoundException(Long id) {
        super("权限不存在: " + id);
    }
}
