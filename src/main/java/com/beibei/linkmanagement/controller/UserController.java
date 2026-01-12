package com.beibei.linkmanagement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beibei.linkmanagement.common.Result;
import com.beibei.linkmanagement.dto.UserCreateRequest;
import com.beibei.linkmanagement.dto.UserDTO;
import com.beibei.linkmanagement.dto.UserUpdateRequest;
import com.beibei.linkmanagement.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final SysUserService sysUserService;
    
    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
    
    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        Page<UserDTO> result = sysUserService.getUserList(page, size, username, role, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        
        return Result.success(data);
    }
    
    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = sysUserService.getUserById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserDTO user = sysUserService.createUser(request);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserDTO user = sysUserService.updateUser(id, request);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            boolean success = sysUserService.deleteUser(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
    
    /**
     * 切换用户状态
     */
    @PutMapping("/{id}/status")
    public Result<UserDTO> toggleUserStatus(@PathVariable Long id) {
        try {
            UserDTO user = sysUserService.toggleUserStatus(id);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = sysUserService.isUsernameExists(username);
        Map<String, Boolean> data = new HashMap<>();
        data.put("exists", exists);
        return Result.success(data);
    }
} 