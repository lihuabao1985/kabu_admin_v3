package com.kabu.admin.user.service.impl;

import com.kabu.admin.user.dto.UserCreateRequest;
import com.kabu.admin.user.dto.UserListResponse;
import com.kabu.admin.user.dto.UserQueryRequest;
import com.kabu.admin.user.dto.UserResponse;
import com.kabu.admin.user.dto.UserUpdateRequest;
import com.kabu.admin.user.exception.UserConflictException;
import com.kabu.admin.user.exception.UserNotFoundException;
import com.kabu.admin.user.model.User;
import com.kabu.admin.user.repository.UserRepository;
import com.kabu.admin.user.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserListResponse listUsers(UserQueryRequest request) {
        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;

        String username = normalizeText(request.username());
        String email = normalizeText(request.email());
        Integer status = normalizeStatus(request.status(), true);
        Integer locked = normalizeLockFlag(request.locked(), true);
        String tenantId = normalizeText(request.tenantId());

        List<UserResponse> items = userRepository.findByCriteria(
            username,
            email,
            status,
            locked,
            tenantId,
            size,
            offset
        )
            .stream()
            .map(this::toResponse)
            .toList();
        long total = userRepository.countByCriteria(username, email, status, locked, tenantId);
        return new UserListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        validateId(id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String username = normalizeUsername(request.username());
        String email = normalizeNullableText(request.email());
        String phone = normalizeNullableText(request.phone());
        String password = requirePassword(request.password());
        ensureUnique(null, username, email, phone);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(normalizeNullableText(request.displayName()));
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPasswordChangedAt(now);
        user.setStatus(normalizeStatus(request.status(), false));
        user.setAccountLocked(0);
        user.setVersion(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        int inserted = userRepository.insert(user);
        if (inserted != 1 || user.getId() == null) {
            throw new IllegalStateException("创建用户失败");
        }
        return getUserById(user.getId());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        validateId(id);
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        String username = normalizeUsername(request.username());
        String email = normalizeNullableText(request.email());
        String phone = normalizeNullableText(request.phone());
        ensureUnique(id, username, email, phone);

        existing.setUsername(username);
        existing.setDisplayName(normalizeNullableText(request.displayName()));
        existing.setEmail(email);
        existing.setPhone(phone);
        String password = normalizeNullableText(request.password());
        if (password != null) {
            existing.setPasswordHash(passwordEncoder.encode(password));
            existing.setPasswordChangedAt(LocalDateTime.now(ZoneOffset.UTC));
        }
        existing.setStatus(request.status() == null ? existing.getStatus() : normalizeStatus(request.status(), false));
        existing.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        int updated = userRepository.update(existing);
        if (updated != 1) {
            if (userRepository.findById(id).isPresent()) {
                throw new IllegalStateException("用户数据版本冲突，id=" + id);
            }
            throw new UserNotFoundException(id);
        }
        return getUserById(id);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, Integer status) {
        validateId(id);
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        int updated = userRepository.updateStatus(id, normalizeStatus(status, false), LocalDateTime.now(ZoneOffset.UTC));
        if (updated != 1) {
            throw new UserNotFoundException(id);
        }
        return getUserById(id);
    }

    @Override
    @Transactional
    public UserResponse updateUserLock(Long id, Integer locked) {
        validateId(id);
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Integer normalizedLocked = normalizeLockFlag(locked, false);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime lockedAt = normalizedLocked == 1 ? now : null;
        int updated = userRepository.updateLock(id, normalizedLocked, lockedAt, now);
        if (updated != 1) {
            throw new UserNotFoundException(id);
        }
        return getUserById(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        validateId(id);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int deleted = userRepository.softDelete(id, now, now);
        if (deleted != 1) {
            throw new UserNotFoundException(id);
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getEmail(),
            user.getPhone(),
            user.getStatus(),
            user.getAccountLocked(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private String normalizeUsername(String username) {
        String normalized = normalizeText(username);
        if (normalized == null) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeNullableText(String value) {
        return normalizeText(value);
    }

    private String requirePassword(String value) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return normalized;
    }

    private Integer normalizeStatus(Integer status, boolean allowNull) {
        if (status == null) {
            return allowNull ? null : 1;
        }
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("状态必须为0或1");
        }
        return status;
    }

    private Integer normalizeLockFlag(Integer locked, boolean allowNull) {
        if (locked == null) {
            return allowNull ? null : 0;
        }
        if (locked != 0 && locked != 1) {
            throw new IllegalArgumentException("锁定状态必须为0或1");
        }
        return locked;
    }

    private void ensureUnique(Long selfId, String username, String email, String phone) {
        userRepository.findByUsername(username)
            .filter(user -> selfId == null || !user.getId().equals(selfId))
            .ifPresent(user -> {
                throw new UserConflictException("username", username);
            });

        if (email != null) {
            userRepository.findByEmail(email)
                .filter(user -> selfId == null || !user.getId().equals(selfId))
                .ifPresent(user -> {
                    throw new UserConflictException("email", email);
                });
        }

        if (phone != null) {
            userRepository.findByPhone(phone)
                .filter(user -> selfId == null || !user.getId().equals(selfId))
                .ifPresent(user -> {
                    throw new UserConflictException("phone", phone);
                });
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID必须为正整数");
        }
    }
}
