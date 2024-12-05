package com.example.security.service;

import com.example.security.model.Role;
import com.example.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleService implements IRoleService{
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(Long id) {
        Role auth = this.roleRepository.getOne(id);
        return auth;
    }

    @Override
    public List<Role> findByName(String name) {
        List<Role> roles = this.roleRepository.findByName(name);
        return roles;
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        List<Role> roles = this.roleRepository.findRolesByUserId(userId);
        return roles;
    }
}
