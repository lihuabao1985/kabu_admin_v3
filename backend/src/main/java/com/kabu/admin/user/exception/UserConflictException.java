package com.kabu.admin.user.exception;

public class UserConflictException extends RuntimeException {

    public UserConflictException(String field, String value) {
        super(toFieldLabel(field) + "已存在: " + value);
    }

    private static String toFieldLabel(String field) {
        return switch (field) {
            case "username" -> "用户名";
            case "email" -> "邮箱";
            case "phone" -> "手机号";
            default -> field;
        };
    }
}
