package com.beibei.linkmanagement.controller;

import com.beibei.linkmanagement.common.Result;
import com.beibei.linkmanagement.dto.LoginRequest;
import com.beibei.linkmanagement.entity.SysUser;
import com.beibei.linkmanagement.service.SysUserService;
import com.beibei.linkmanagement.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    
    public AuthController(SysUserService sysUserService, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserService.getUserByUsername(request.getUsername());
        if (user == null || !sysUserService.validatePassword(request.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        
        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            return Result.error(403, "用户已被禁用");
        }
        
        // 更新最后登录时间
        sysUserService.updateLastLoginTime(user.getId());
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());
        userInfo.put("createdAt", user.getCreatedAt());
        userInfo.put("lastLoginAt", user.getLastLoginAt());
        
        responseData.put("userInfo", userInfo);
        
        return Result.success(responseData);
    }
} 