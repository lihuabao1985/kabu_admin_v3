package com.kabu.admin.common.api;

import com.kabu.admin.permission.exception.PermissionConflictException;
import com.kabu.admin.permission.exception.PermissionNotFoundException;
import com.kabu.admin.role.exception.RoleConflictException;
import com.kabu.admin.role.exception.RoleNotFoundException;
import com.kabu.admin.role.exception.SystemRoleOperationException;
import com.kabu.admin.stock.exception.StockConflictException;
import com.kabu.admin.stock.exception.StockFavoriteConflictException;
import com.kabu.admin.stock.exception.StockFavoriteNotFoundException;
import com.kabu.admin.stock.exception.StockNotFoundException;
import com.kabu.admin.stock.dividendconfirmed.exception.StockDividendConfirmedConflictException;
import com.kabu.admin.stock.dividendconfirmed.exception.StockDividendConfirmedNotFoundException;
import com.kabu.admin.stock.pricehistory.exception.StockPriceHistoryConflictException;
import com.kabu.admin.stock.pricehistory.exception.StockPriceHistoryNotFoundException;
import com.kabu.admin.user.exception.UserConflictException;
import com.kabu.admin.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UserConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserConflict(UserConflictException ex) {
        return new ErrorResponse("USER_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoleNotFound(RoleNotFoundException ex) {
        return new ErrorResponse("ROLE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(RoleConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRoleConflict(RoleConflictException ex) {
        return new ErrorResponse("ROLE_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePermissionNotFound(PermissionNotFoundException ex) {
        return new ErrorResponse("PERMISSION_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(PermissionConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePermissionConflict(PermissionConflictException ex) {
        return new ErrorResponse("PERMISSION_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(StockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStockNotFound(StockNotFoundException ex) {
        return new ErrorResponse("STOCK_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(StockConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStockConflict(StockConflictException ex) {
        return new ErrorResponse("STOCK_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(StockFavoriteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStockFavoriteNotFound(StockFavoriteNotFoundException ex) {
        return new ErrorResponse("STOCK_FAVORITE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(StockFavoriteConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStockFavoriteConflict(StockFavoriteConflictException ex) {
        return new ErrorResponse("STOCK_FAVORITE_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(StockPriceHistoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStockPriceHistoryNotFound(StockPriceHistoryNotFoundException ex) {
        return new ErrorResponse("STOCK_PRICE_HISTORY_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(StockPriceHistoryConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStockPriceHistoryConflict(StockPriceHistoryConflictException ex) {
        return new ErrorResponse("STOCK_PRICE_HISTORY_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(StockDividendConfirmedNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStockDividendConfirmedNotFound(StockDividendConfirmedNotFoundException ex) {
        return new ErrorResponse("STOCK_DIVIDEND_CONFIRMED_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(StockDividendConfirmedConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStockDividendConfirmedConflict(StockDividendConfirmedConflictException ex) {
        return new ErrorResponse("STOCK_DIVIDEND_CONFIRMED_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(SystemRoleOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSystemRoleOperation(SystemRoleOperationException ex) {
        return new ErrorResponse("SYSTEM_ROLE_OPERATION_NOT_ALLOWED", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex) {
        return new ErrorResponse("FORBIDDEN", "无权访问");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        return new ErrorResponse("INTERNAL_ERROR", "系统异常");
    }
}
