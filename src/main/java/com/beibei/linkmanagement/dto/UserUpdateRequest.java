package com.beibei.linkmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含字母、数字和下划线，长度3-20位")
    private String username;
    
    @Size(min = 2, max = 20, message = "昵称长度在2-20个字符")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^.{6,20}$", message = "密码长度6-20位")
    private String password;
    
    @Pattern(regexp = "^(ADMIN|USER|OPERATOR)$", message = "角色只能是ADMIN、USER或OPERATOR")
    private String role;
    
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "状态只能是ACTIVE或INACTIVE")
    private String status;
} 