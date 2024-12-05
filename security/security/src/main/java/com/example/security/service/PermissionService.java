package com.example.security.service;

import com.example.security.model.Permission;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.PermissionRepository;
import com.example.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService implements IPermissionService{
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public Boolean hasPermission(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Provera da li je autentičan token
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            String email = jwt.getClaimAsString("email");
            System.out.println("Email: " + email);

            Boolean hasPermission = false;
            User u = this.userService.findByEmail(email);
            System.out.println("Roles: " + u.getRoles());
            for (Role r : u.getRoles()) {
                System.out.println("Role: " + r.getName());
                System.out.println("Permission: " + r.getPermissions().size());
                for (Permission p : r.getPermissions()) {
                    System.out.println("Role: " + p.getName()+" "+permission);
                    if (p.getName().equals(permission))
                        hasPermission = true;
                }
            }
            System.out.println("Roles: "+hasPermission);
            return hasPermission;
        } else {
            throw new RuntimeException("JWT token is not present");
        }

    }
    @Override
    public List<Permission> findAll() {
        return this.permissionRepository.findAll();
    }

    @Override
    public List<Permission> existingForRole(String role) {
        List<Permission> permissions= new ArrayList<>();
        List<Role> roles=this.roleService.findByName(role);
        for(Role r:roles){
            if(r.getName().equals(role)){
                permissions=r.getPermissions();
            }
        }
        return permissions;
    }

    @Override
    public List<Permission> unexistingForRole(String role) {
        List<Permission> permissions=existingForRole(role);
        List<Permission> storedPermissions=this.permissionRepository.findAll();
        List<Permission> unexistingPermission=new ArrayList<>();
        boolean exists=false;
        for(Permission p:storedPermissions){
            for(Permission p1:permissions){
                if(p.getId().equals(p1.getId()))
                    exists=true;
            }
            if(!exists){
                unexistingPermission.add(p);
            }
            exists=false;

        }
        return unexistingPermission;
    }

    @Override
    public Permission addPermision(String name, String role) {
        List<Role> roles=roleService.findByName(role);
        Permission permission=permissionRepository.findByName(name);
        List<Permission> permissions=roles.get(0).getPermissions();
        permissions.add(permission);
        roles.get(0).setPermissions(permissions);
        this.roleRepository.save(roles.get(0));
        return permission;
    }

    @Override
    public Permission removePermission(String name, String role) {
        List<Role> roles=roleService.findByName(role);
        Permission permission=permissionRepository.findByName(name);
        List<Permission> permissions=roles.get(0).getPermissions();
        permissions.removeIf(permission1 -> permission1.getName().equals(name));
        roles.get(0).setPermissions(permissions);
        this.roleRepository.save(roles.get(0));
        return permission;
    }


}
