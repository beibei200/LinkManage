package com.beibei.linkmanagement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beibei.linkmanagement.dto.UserCreateRequest;
import com.beibei.linkmanagement.dto.UserDTO;
import com.beibei.linkmanagement.dto.UserUpdateRequest;
import com.beibei.linkmanagement.entity.SysUser;

public interface SysUserService {
    SysUser getUserByUsername(String username);
    
    SysUser createUser(String username, String password, String role);
    
    boolean validatePassword(String rawPassword, String encodedPassword);
    
    Page<UserDTO> getUserList(Integer page, Integer size, String username, String role, String status);
    
    UserDTO getUserById(Long id);
    
    UserDTO createUser(UserCreateRequest request);
    
    UserDTO updateUser(Long id, UserUpdateRequest request);
    
    boolean deleteUser(Long id);
    
    boolean isUsernameExists(String username);
    
    SysUser getUserEntityById(Long id);
    
    UserDTO toggleUserStatus(Long id);
    
    void updateLastLoginTime(Long userId);
} 