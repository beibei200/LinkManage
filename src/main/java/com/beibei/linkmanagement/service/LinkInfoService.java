package com.beibei.linkmanagement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.beibei.linkmanagement.common.PageRequest;
import com.beibei.linkmanagement.entity.LinkInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LinkInfoService extends IService<LinkInfo> {
    Page<LinkInfo> getLinks(PageRequest pageRequest, String linkType, String ipAddress, String regionPath, String vpsProvider, String purchaser,
                            LocalDateTime purchaseTimeStart, LocalDateTime purchaseTimeEnd,
                            LocalDateTime expireTimeStart, LocalDateTime expireTimeEnd);

    List<LinkInfo> getExportData(String linkType, String ipAddress,
                                       String regionPath, String vpsProvider, String purchaser,
                                       LocalDateTime purchaseTimeStart, LocalDateTime purchaseTimeEnd,
                                       LocalDateTime expireTimeStart, LocalDateTime expireTimeEnd);

    Map<String, Object> getStats();
} 