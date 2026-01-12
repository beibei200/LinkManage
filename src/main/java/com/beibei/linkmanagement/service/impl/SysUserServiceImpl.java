package com.beibei.linkmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beibei.linkmanagement.dto.UserCreateRequest;
import com.beibei.linkmanagement.dto.UserDTO;
import com.beibei.linkmanagement.dto.UserUpdateRequest;
import com.beibei.linkmanagement.entity.SysUser;
import com.beibei.linkmanagement.mapper.SysUserMapper;
import com.beibei.linkmanagement.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {
    
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    
    public SysUserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional
    public SysUser createUser(String username, String password, String role) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(username + "@example.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus("ACTIVE");
        sysUserMapper.insert(user);
        return user;
    }
    
    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public Page<UserDTO> getUserList(Integer page, Integer size, String username, String role, String status) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreatedAt);
        
        Page<SysUser> result = sysUserMapper.selectPage(pageParam, wrapper);
        
        // 转换为DTO
        List<UserDTO> userDTOs = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Page<UserDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(userDTOs);
        
        return dtoPage;
    }
    
    @Override
    public UserDTO getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        return user != null ? convertToDTO(user) : null;
    }
    
    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        
        sysUserMapper.insert(user);
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否已被其他用户使用
        if (StringUtils.hasText(request.getUsername()) && 
            !request.getUsername().equals(user.getUsername()) && 
            isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 更新字段
        if (StringUtils.hasText(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(request.getRole());
        }
        if (StringUtils.hasText(request.getStatus())) {
            user.setStatus(request.getStatus());
        }
        
        sysUserMapper.updateById(user);
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        // 检查是否为最后一个管理员
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return false;
        }
        
        if ("ADMIN".equals(user.getRole())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getRole, "ADMIN");
            long adminCount = sysUserMapper.selectCount(wrapper);
            if (adminCount <= 1) {
                throw new RuntimeException("不能删除最后一个管理员用户");
            }
        }
        
        return sysUserMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public SysUser getUserEntityById(Long id) {
        return sysUserMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public UserDTO toggleUserStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 切换状态
        String newStatus = "ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE";
        user.setStatus(newStatus);
        
        sysUserMapper.updateById(user);
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public void updateLastLoginTime(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            sysUserMapper.updateById(user);
        }
    }
    
    private UserDTO convertToDTO(SysUser user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
} 