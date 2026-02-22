package com.kabu.admin.permission.exception;

public class PermissionConflictException extends RuntimeException {

    public PermissionConflictException(String permissionCode) {
        super("权限编码已存在: " + permissionCode);
    }
}
