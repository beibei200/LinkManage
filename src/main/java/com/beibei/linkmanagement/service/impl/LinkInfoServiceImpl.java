package com.beibei.linkmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beibei.linkmanagement.common.PageRequest;
import com.beibei.linkmanagement.entity.LinkInfo;
import com.beibei.linkmanagement.mapper.LinkInfoMapper;
import com.beibei.linkmanagement.service.LinkInfoService;
//import com.beibei.linkmanagement.vo.LinkInfoExportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LinkInfoServiceImpl extends ServiceImpl<LinkInfoMapper, LinkInfo> implements LinkInfoService {

    @Autowired
    private LinkInfoMapper linkInfoMapper;

    @Override
    public Page<LinkInfo> getLinks(PageRequest pageRequest, String linkType, String ipAddress, String regionPath, String vpsProvider, String purchaser, LocalDateTime purchaseTimeStart, LocalDateTime purchaseTimeEnd, LocalDateTime expireTimeStart, LocalDateTime expireTimeEnd) {
        Page<LinkInfo> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        QueryWrapper<LinkInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.hasText(linkType)) {
            queryWrapper.eq("link_type", linkType);
        }
        if (StringUtils.hasText(ipAddress)) {
            queryWrapper.and(wrapper -> wrapper.like("ip_address", ipAddress)
                    .or()
                    .like("ip_address_2", ipAddress));
        }
        if (StringUtils.hasText(regionPath)) {
            queryWrapper.like("region_path", regionPath);
        }
        if (StringUtils.hasText(vpsProvider)) {
            queryWrapper.like("vps_provider", vpsProvider);
        }
        if (StringUtils.hasText(purchaser)) {
            queryWrapper.like("purchaser", purchaser);
        }
        if (purchaseTimeStart != null) {
            queryWrapper.ge("purchase_time", purchaseTimeStart);
        }
        if (purchaseTimeEnd != null) {
            queryWrapper.le("purchase_time", purchaseTimeEnd);
        }
        if (expireTimeStart != null) {
            queryWrapper.ge("expire_time", expireTimeStart);
        }
        if (expireTimeEnd != null) {
            queryWrapper.le("expire_time", expireTimeEnd);
        }

        queryWrapper.orderByDesc("id");

        return linkInfoMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<LinkInfo> getExportData(String linkType, String ipAddress, String regionPath, String vpsProvider, String purchaser, LocalDateTime purchaseTimeStart, LocalDateTime purchaseTimeEnd, LocalDateTime expireTimeStart, LocalDateTime expireTimeEnd) {
        QueryWrapper<LinkInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.hasText(linkType)) {
            queryWrapper.eq("link_type", linkType);
        }
        if (StringUtils.hasText(ipAddress)) {
            queryWrapper.and(wrapper -> wrapper.like("ip_address", ipAddress).or().like("ip_address_2", ipAddress));
        }
        if (StringUtils.hasText(regionPath)) {
            queryWrapper.like("region_path", regionPath);
        }
        if (StringUtils.hasText(vpsProvider)) {
            queryWrapper.like("vps_provider", vpsProvider);
        }
        if (StringUtils.hasText(purchaser)) {
            queryWrapper.like("purchaser", purchaser);
        }
        if (purchaseTimeStart != null) {
            queryWrapper.ge("purchase_time", purchaseTimeStart);
        }
        if (purchaseTimeEnd != null) {
            queryWrapper.le("purchase_time", purchaseTimeEnd);
        }
        if (expireTimeStart != null) {
            queryWrapper.ge("expire_time", expireTimeStart);
        }
        if (expireTimeEnd != null) {
            queryWrapper.le("expire_time", expireTimeEnd);
        }
        queryWrapper.orderByDesc("id");

        return linkInfoMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, Object> getStats() {
        return linkInfoMapper.getStats();
    }

    /*
    private LinkInfoExportVO convertToExportVO(LinkInfo linkInfo) {
        LinkInfoExportVO vo = new LinkInfoExportVO();
        vo.setId(linkInfo.getId());
        vo.setLinkType(linkInfo.getLinkType().equals("PRIMARY") ? "一级链路" : "多级链路");
        vo.setIpAddress(linkInfo.getIpAddress());
        vo.setIpAddress2(linkInfo.getIpAddress2());
        vo.setRegionPath(linkInfo.getRegionPath());
        vo.setJumpCount(linkInfo.getJumpCount());
        vo.setVpsProvider(linkInfo.getVpsProvider());
        vo.setPurchaser(linkInfo.getPurchaser());
        vo.setPrice(linkInfo.getPrice() != null ? linkInfo.getPrice() : new java.math.BigDecimal("0.00"));
        vo.setPurchaseTime(linkInfo.getPurchaseTime() != null ? Date.from(linkInfo.getPurchaseTime().atZone(ZoneId.systemDefault()).toInstant()) : null);
        vo.setExpireTime(linkInfo.getExpireTime() != null ? Date.from(linkInfo.getExpireTime().atZone(ZoneId.systemDefault()).toInstant()) : null);
        return vo;
    }
    */
} 