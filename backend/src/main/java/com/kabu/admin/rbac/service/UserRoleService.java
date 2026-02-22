package com.kabu.admin.rbac.service;

import com.kabu.admin.rbac.dto.UserRoleListResponse;
import java.util.List;

public interface UserRoleService {

    UserRoleListResponse listUserRoles(Long userId);

    UserRoleListResponse replaceUserRoles(Long userId, List<Long> roleIds);

    UserRoleListResponse addUserRoles(Long userId, List<Long> roleIds);

    UserRoleListResponse removeUserRoles(Long userId, List<Long> roleIds);
}
