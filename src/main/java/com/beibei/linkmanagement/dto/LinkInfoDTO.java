package com.beibei.linkmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LinkInfoDTO {
    private Long id;
    
    @NotBlank(message = "链路类型不能为空")
    private String linkType;

    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$", 
             message = "请输入合法的IP地址")
    private String ipAddress;

    // @Pattern(regexp = "^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$", 
    //          message = "请输入合法的IP地址")
    private String ipAddress2;

    @NotBlank(message = "跳转地区不能为空")
    private String regionPath;

    @NotNull(message = "购买时间不能为空")
    private LocalDateTime purchaseTime;
    
    @NotNull(message = "到期时间不能为空")
    private LocalDateTime expireTime;
    
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;
    
    @NotBlank(message = "VPS商家信息不能为空")
    private String vpsProvider;
    
    @NotBlank(message = "采购者不能为空")
    private String purchaser;
    
    @NotNull(message = "跳转次数不能为空")
    @Min(value = 1, message = "跳转次数不能小于1")
    @Max(value = 4, message = "跳转次数不能大于4")
    private Integer jumpCount;
} 