package com.beibei.linkmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("link_info")
public class LinkInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String linkType;

    @TableField("ip_address")
    private String ipAddress;
    
    @TableField("ip_address_2")
    private String ipAddress2;

    @TableField("region_path")
    private String regionPath;

    private LocalDateTime purchaseTime;
    
    private LocalDateTime expireTime;
    
    private BigDecimal price;
    
    private String vpsProvider;
    
    private String purchaser;
    
    private Integer jumpCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
} 