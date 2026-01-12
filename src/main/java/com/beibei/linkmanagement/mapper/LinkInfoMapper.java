package com.beibei.linkmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beibei.linkmanagement.entity.LinkInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface LinkInfoMapper extends BaseMapper<LinkInfo> {
    @Select("SELECT " +
            "COUNT(*) as totalLinks, " +
            "SUM(CASE WHEN link_type = 'PRIMARY' THEN 1 ELSE 0 END) as primaryLinks, " +
            "SUM(CASE WHEN link_type = 'SECONDARY' THEN 1 ELSE 0 END) as secondaryLinks, " +
            "SUM(price) as totalPrice " +
            "FROM link_info WHERE deleted = 0")
    Map<String, Object> getStats();
} 