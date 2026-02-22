package com.kabu.admin.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserShouldPersistAndReturnCreatedUser() {
        UserCreateRequest request = new UserCreateRequest(" alice ", "Alice", "alice@example.com", null);
        User stored = buildUser(1L, "alice", "Alice", "alice@example.com", 1);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
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

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).insert(userCaptor.capture());
        assertEquals("alice", userCaptor.getValue().getUsername());
        assertEquals(1, userCaptor.getValue().getStatus());
    }

    @Test
    void createUserShouldFailWhenUsernameExists() {
        UserCreateRequest request = new UserCreateRequest("alice", "Alice", "alice@example.com", 1);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(buildUser(2L, "alice", "Alice", "a@b.c", 1)));

        assertThrows(UserConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> userService.updateUser(100L, new UserUpdateRequest("alice", "Alice", "alice@example.com", 1))
        );
    }

    @Test
    void updateUserShouldFailWhenTargetUsernameExists() {
        User existing = buildUser(1L, "alice", "Alice", "alice@example.com", 1);
        User another = buildUser(2L, "bob", "Bob", "bob@example.com", 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(another));

        assertThrows(
            UserConflictException.class,
            () -> userService.updateUser(1L, new UserUpdateRequest("bob", "Alice", "alice@example.com", 1))
        );
    }

    @Test
    void deleteUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.deleteById(99L)).thenReturn(0);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void listUsersShouldReturnPagedData() {
        when(userRepository.findByCriteria("alice", null, 1, 10, 0))
            .thenReturn(List.of(buildUser(1L, "alice", "Alice", "alice@example.com", 1)));
        when(userRepository.countByCriteria("alice", null, 1)).thenReturn(1L);

        UserListResponse response = userService.listUsers(new UserQueryRequest("alice", null, 1, 1, 10));

        assertEquals(1, response.items().size());
        assertEquals(1, response.total());
        assertEquals(1, response.page());
        assertEquals(10, response.size());
        assertTrue(response.items().get(0).username().contains("alice"));
    }

    private User buildUser(Long id, String username, String displayName, String email, Integer status) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
