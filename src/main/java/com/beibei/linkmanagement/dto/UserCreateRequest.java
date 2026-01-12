package com.beibei.linkmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含字母、数字和下划线，长度3-20位")
    private String username;
    
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度在2-20个字符")
    private String nickname;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度6-20位")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(ADMIN|USER|OPERATOR)$", message = "角色只能是ADMIN、USER或OPERATOR")
    private String role;
    
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "状态只能是ACTIVE或INACTIVE")
    private String status = "ACTIVE";
} 