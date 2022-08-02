package com.techsoft.api.authentication.service;

import com.techsoft.api.authentication.domain.Role;
import com.techsoft.api.authentication.form.RoleForm;
import com.techsoft.api.authentication.repository.RoleRepository;
import com.techsoft.api.common.service.impl.AbstractCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends AbstractCrudServiceImpl<Role, Long, RoleRepository, RoleForm> {

    @Autowired
    RoleService(RoleRepository roleRepository) {
        super(roleRepository, Role.class);
    }
}
