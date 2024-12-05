package com.example.security.controller;

import com.example.security.dto.PermissionDto;
import com.example.security.model.Advertisement;
import com.example.security.model.AdvertisementRequest;
import com.example.security.model.Permission;
import com.example.security.service.IAdvertisementService;
import com.example.security.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/permission", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class PermissionController {
    @Autowired
    private IPermissionService permissionService;
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/existing/{role}")
    public List<Permission> getExisting(@PathVariable("role") String role) {
        return permissionService.existingForRole(role);
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/unexisting/{role}")
    public List<Permission> getUnexisting(@PathVariable("role") String role) {
        return permissionService.unexistingForRole(role);
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/add/{name}/{role}")
    public ResponseEntity<PermissionDto> addPermission(@PathVariable("name") String name, @PathVariable("role") String role) {
        Permission permission=permissionService.addPermision(name,role);
        PermissionDto permissionDto=new PermissionDto(permission.getName());
        return ResponseEntity.ok(permissionDto);
    }
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/remove/{name}/{role}")
    public ResponseEntity<PermissionDto> removePermission(@PathVariable("name") String name, @PathVariable("role") String role) {
        Permission permission=permissionService.removePermission(name,role);
        PermissionDto permissionDto=new PermissionDto(permission.getName());
        return ResponseEntity.ok(permissionDto);
    }
}
