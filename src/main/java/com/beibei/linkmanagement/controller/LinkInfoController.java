package com.beibei.linkmanagement.controller;

import com.beibei.linkmanagement.common.PageRequest;
import com.beibei.linkmanagement.common.Result;
import com.beibei.linkmanagement.dto.LinkInfoDTO;
import com.beibei.linkmanagement.entity.LinkInfo;
import com.beibei.linkmanagement.service.LinkInfoService;
//import com.beibei.linkmanagement.vo.LinkInfoExportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/links")
public class LinkInfoController {

    private static final Logger log = LoggerFactory.getLogger(LinkInfoController.class);

    @Autowired
    private LinkInfoService linkInfoService;

    private String escapeCsv(String data) {
        if (data == null) {
            return "";
        }
        if (data.contains(",")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }

    private String convertToCsvRow(String[] data) {
        return Stream.of(data)
                .map(this::escapeCsv)
                .collect(Collectors.joining(","));
    }

    @GetMapping("/export")
    public void exportLinks(
            HttpServletResponse response,
            @RequestParam(required = false) String exportMode,
            @RequestParam(required = false) String linkType,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String regionPath,
            @RequestParam(required = false) String vpsProvider,
            @RequestParam(required = false) String purchaser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime purchaseTimeStart,
            @RequestParam(required =false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime purchaseTimeEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireTimeStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireTimeEnd
    ) {
        response.setContentType("text/csv;charset=UTF-8");
        String fileName = URLEncoder.encode("链路数据.csv", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        try (OutputStream os = response.getOutputStream()) {
            // Write BOM for Excel compatibility by writing raw bytes
            os.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
            os.flush();

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                // Write header
                String[] headers = {"ID", "链路等级", "IP地址", "多级IP", "跳转地区", "跳转次数", "VPS商家", "采购者", "价格(元)", "购买时间", "到期时间"};
                writer.println(convertToCsvRow(headers));

                // Get data
                List<LinkInfo> data;
                if ("all".equals(exportMode)) {
                    data = linkInfoService.getExportData(null, null, null, null, null, null, null, null, null);
                } else {
                    data = linkInfoService.getExportData(linkType, ipAddress, regionPath, vpsProvider, purchaser, purchaseTimeStart, purchaseTimeEnd, expireTimeStart, expireTimeEnd);
                }

                // Write data rows
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (LinkInfo linkInfo : data) {
                    String[] rowData = {
                            linkInfo.getId() != null ? linkInfo.getId().toString() : "",
                            "PRIMARY".equals(linkInfo.getLinkType()) ? "一级链路" : "多级链路",
                            linkInfo.getIpAddress(),
                            linkInfo.getIpAddress2(),
                            linkInfo.getRegionPath(),
                            linkInfo.getJumpCount() != null ? linkInfo.getJumpCount().toString() : "",
                            linkInfo.getVpsProvider(),
                            linkInfo.getPurchaser(),
                            linkInfo.getPrice() != null ? linkInfo.getPrice().toString() : "",
                            linkInfo.getPurchaseTime() != null ? linkInfo.getPurchaseTime().format(formatter) : "",
                            linkInfo.getExpireTime() != null ? linkInfo.getExpireTime().format(formatter) : ""
                    };
                    writer.println(convertToCsvRow(rowData));
                }
                writer.flush();
            }
        } catch (Exception e) {
            log.error("使用CSV导出时发生错误", e);
        }
    }

    @GetMapping
    public Result<Map<String, Object>> getLinks(
            PageRequest pageRequest,
            @RequestParam(required = false) String linkType,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String regionPath,
            @RequestParam(required = false) String vpsProvider,
            @RequestParam(required = false) String purchaser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime purchaseTimeStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime purchaseTimeEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireTimeStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireTimeEnd
    ) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LinkInfo> pageResult = linkInfoService.getLinks(pageRequest, linkType, ipAddress, regionPath, vpsProvider, purchaser, purchaseTimeStart, purchaseTimeEnd, expireTimeStart, expireTimeEnd);
        
        // 转换为前端期望的Spring Data JPA Page格式
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("content", pageResult.getRecords());
        responseData.put("pageable", new HashMap<String, Object>() {{
            put("sort", new HashMap<String, Boolean>() {{
                put("sorted", !pageResult.orders().isEmpty());
                put("unsorted", pageResult.orders().isEmpty());
                put("empty", pageResult.orders().isEmpty());
            }});
            put("offset", pageResult.offset());
            put("pageNumber", pageResult.getCurrent());
            put("pageSize", pageResult.getSize());
            put("paged", true);
            put("unpaged", false);
        }});
        responseData.put("last", pageResult.getCurrent() >= pageResult.getPages());
        responseData.put("totalPages", pageResult.getPages());
        responseData.put("totalElements", pageResult.getTotal());
        responseData.put("size", pageResult.getSize());
        responseData.put("number", pageResult.getCurrent());
        responseData.put("sort", new HashMap<String, Boolean>() {{
            put("sorted", !pageResult.orders().isEmpty());
            put("unsorted", pageResult.orders().isEmpty());
            put("empty", pageResult.orders().isEmpty());
        }});
        responseData.put("first", pageResult.getCurrent() == 1);
        responseData.put("numberOfElements", pageResult.getRecords().size());
        responseData.put("empty", pageResult.getRecords().isEmpty());

        return Result.success(responseData);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = linkInfoService.getStats();
        return Result.success(stats);
    }

    @PostMapping
    public Result<LinkInfo> createLink(@Validated @RequestBody LinkInfoDTO linkInfoDTO) {
        LinkInfo linkInfo = new LinkInfo();
        BeanUtils.copyProperties(linkInfoDTO, linkInfo);
        linkInfoService.save(linkInfo);
        return Result.success(linkInfo);
    }

    @PutMapping("/{id}")
    public Result<LinkInfo> updateLink(@PathVariable Long id, @Validated @RequestBody LinkInfoDTO linkInfoDTO) {
        LinkInfo linkInfo = new LinkInfo();
        BeanUtils.copyProperties(linkInfoDTO, linkInfo);
        linkInfo.setId(id);
        linkInfoService.updateById(linkInfo);
        return Result.success(linkInfo);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteLink(@PathVariable Long id) {
        linkInfoService.removeById(id);
        return Result.success(null);
    }
}