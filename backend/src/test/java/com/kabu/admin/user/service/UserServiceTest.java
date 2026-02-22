package com.kabu.admin.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kabu.admin.user.dto.UserCreateRequest;
import com.kabu.admin.user.dto.UserListResponse;
import com.kabu.admin.user.dto.UserQueryRequest;
import com.kabu.admin.user.dto.UserUpdateRequest;
import com.kabu.admin.user.exception.UserConflictException;
import com.kabu.admin.user.exception.UserNotFoundException;
import com.kabu.admin.user.model.User;
import com.kabu.admin.user.repository.UserRepository;
import com.kabu.admin.user.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserShouldPersistAndReturnCreatedUser() {
        UserCreateRequest request = new UserCreateRequest(
            " alice ",
            "Alice",
            "alice@example.com",
            "10000000001",
            "123456",
            null
        );
        User stored = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 0);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("10000000001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$hash");
        when(userRepository.insert(any(User.class))).thenAnswer(invocation -> {
            User argument = invocation.getArgument(0);
            argument.setId(1L);
            return 1;
        });
        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = userService.createUser(request);

        assertEquals(1L, response.id());
        assertEquals("alice", response.username());
        assertEquals(1, response.status());
        assertEquals(0, response.accountLocked());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).insert(userCaptor.capture());
        assertEquals("alice", userCaptor.getValue().getUsername());
        assertEquals("10000000001", userCaptor.getValue().getPhone());
        assertEquals("$2a$10$hash", userCaptor.getValue().getPasswordHash());
    }

    @Test
    void createUserShouldFailWhenUsernameExists() {
        UserCreateRequest request = new UserCreateRequest(
            "alice",
            "Alice",
            "alice@example.com",
            "10000000001",
            "123456",
            1
        );
        when(userRepository.findByUsername("alice")).thenReturn(
            Optional.of(buildUser(2L, "alice", "Alice", "a@b.c", "10000000099", 1, 0))
        );

        assertThrows(UserConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void createUserShouldFailWhenEmailExists() {
        UserCreateRequest request = new UserCreateRequest(
            "alice",
            "Alice",
            "alice@example.com",
            "10000000001",
            "123456",
            1
        );
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("alice@example.com")).thenReturn(
            Optional.of(buildUser(2L, "bob", "Bob", "alice@example.com", "10000000088", 1, 0))
        );

        assertThrows(UserConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> userService.updateUser(
                100L,
                new UserUpdateRequest("alice", "Alice", "alice@example.com", "10000000001", null, 1)
            )
        );
    }

    @Test
    void updateUserShouldFailWhenTargetPhoneExists() {
        User existing = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 0);
        User another = buildUser(2L, "bob", "Bob", "bob@example.com", "10000000002", 1, 0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.findByPhone("10000000002")).thenReturn(Optional.of(another));

        assertThrows(
            UserConflictException.class,
            () -> userService.updateUser(
                1L,
                new UserUpdateRequest("alice", "Alice", "alice@example.com", "10000000002", null, 1)
            )
        );
    }

    @Test
    void updateUserStatusShouldPersist() {
        User enabled = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 0);
        User disabled = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 0, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(enabled), Optional.of(disabled));
        when(userRepository.updateStatus(eq(1L), eq(0), any(LocalDateTime.class))).thenReturn(1);

        var response = userService.updateUserStatus(1L, 0);

        assertEquals(0, response.status());
    }

    @Test
    void updateUserLockShouldPersist() {
        User unlocked = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 0);
        User locked = buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(unlocked), Optional.of(locked));
        when(userRepository.updateLock(eq(1L), eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(1);

        var response = userService.updateUserLock(1L, 1);

        assertEquals(1, response.accountLocked());
    }

    @Test
    void deleteUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.softDelete(eq(99L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void listUsersShouldReturnPagedData() {
        when(userRepository.findByCriteria("alice", null, 1, 0, "tenant-a", 10, 0))
            .thenReturn(List.of(buildUser(1L, "alice", "Alice", "alice@example.com", "10000000001", 1, 0)));
        when(userRepository.countByCriteria("alice", null, 1, 0, "tenant-a")).thenReturn(1L);

        UserListResponse response = userService.listUsers(
            new UserQueryRequest("alice", null, 1, 0, "tenant-a", 1, 10)
        );

        assertEquals(1, response.items().size());
        assertEquals(1, response.total());
        assertEquals(1, response.page());
        assertEquals(10, response.size());
        assertTrue(response.items().get(0).username().contains("alice"));
    }

    private User buildUser(
        Long id,
        String username,
        String displayName,
        String email,
        String phone,
        Integer status,
        Integer accountLocked
    ) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(status);
        user.setAccountLocked(accountLocked);
        user.setVersion(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
