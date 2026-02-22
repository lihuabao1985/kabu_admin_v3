package com.kabu.admin.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kabu.admin.rbac.repository.UserRoleRepository;
import com.kabu.admin.rbac.service.impl.UserRoleServiceImpl;
import com.kabu.admin.role.service.RoleService;
import com.kabu.admin.user.model.User;
import com.kabu.admin.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @Test
    void listUserRolesShouldReturnRoleIds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(userRoleRepository.findRoleIdsByUserId(1L)).thenReturn(List.of(1L, 2L));

        var response = userRoleService.listUserRoles(1L);

        assertEquals(List.of(1L, 2L), response.roleIds());
    }

    @Test
    void addUserRolesShouldFailWhenSomeRoleIdsDoNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(roleService.findExistingIds(List.of(1L, 2L))).thenReturn(List.of(1L));

        assertThrows(IllegalArgumentException.class, () -> userRoleService.addUserRoles(1L, List.of(1L, 2L)));
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user");
        user.setStatus(1);
        return user;
    }
}
