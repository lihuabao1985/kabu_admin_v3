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
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        List<UserResponse> items = userRepository.findByCriteria(username, email, status, size, offset)
            .stream()
            .map(this::toResponse)
            .toList();
        long total = userRepository.countByCriteria(username, email, status);
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
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserConflictException(username);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(normalizeNullableText(request.displayName()));
        user.setEmail(normalizeNullableText(request.email()));
        user.setStatus(normalizeStatus(request.status(), false));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        int inserted = userRepository.insert(user);
        if (inserted != 1 || user.getId() == null) {
            throw new IllegalStateException("Failed to create user");
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
        userRepository.findByUsername(username)
            .filter(other -> !other.getId().equals(id))
            .ifPresent(other -> {
                throw new UserConflictException(username);
            });

        existing.setUsername(username);
        existing.setDisplayName(normalizeNullableText(request.displayName()));
        existing.setEmail(normalizeNullableText(request.email()));
        existing.setStatus(request.status() == null ? existing.getStatus() : normalizeStatus(request.status(), false));
        existing.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        int updated = userRepository.update(existing);
        if (updated != 1) {
            throw new UserNotFoundException(id);
        }
        return getUserById(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        validateId(id);
        int deleted = userRepository.deleteById(id);
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
            user.getStatus(),
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
            throw new IllegalArgumentException("username is required");
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

    private Integer normalizeStatus(Integer status, boolean allowNull) {
        if (status == null) {
            return allowNull ? null : 1;
        }
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("status must be 0 or 1");
        }
        return status;
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("id must be positive");
        }
    }
}
