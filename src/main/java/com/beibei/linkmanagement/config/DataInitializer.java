package com.beibei.linkmanagement.config;

import com.beibei.linkmanagement.entity.LinkInfo;
import com.beibei.linkmanagement.entity.SysUser;
import com.beibei.linkmanagement.service.LinkInfoService;
import com.beibei.linkmanagement.service.SysUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final SysUserService sysUserService;
    private final LinkInfoService linkInfoService;
    
    public DataInitializer(SysUserService sysUserService, LinkInfoService linkInfoService) {
        this.sysUserService = sysUserService;
        this.linkInfoService = linkInfoService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 检查核心用户'admin'是否存在，如果存在，则假定数据已初始化，跳过此过程
        if (sysUserService.getUserByUsername("admin") != null) {
            System.out.println("检测到已有管理员用户，跳过Java数据初始化。");
            return;
        }
        
        System.out.println("首次启动，开始进行数据初始化...");

        // 创建用户
        sysUserService.createUser("admin", "admin123", "ADMIN");
        System.out.println("✅ 管理员用户创建成功: admin/admin123");
        
        sysUserService.createUser("testuser", "admin123", "USER");
        System.out.println("✅ 测试用户创建成功: testuser/admin123");
        
        sysUserService.createUser("operator", "admin123", "OPERATOR");
        System.out.println("✅ 操作员用户创建成功: operator/admin123");
        
        // 创建测试链路数据
        createTestLinks();
        System.out.println("✅ 测试链路数据创建成功");
        
        System.out.println("🚀 数据初始化完成！");
        System.out.println("📖 API文档: http://localhost:8081/swagger-ui.html");
        System.out.println("🔐 测试账号 (密码均为: admin123):");
        System.out.println("   - 管理员: admin");
        System.out.println("   - 普通用户: testuser");
        System.out.println("   - 操作员: operator");
    }
    
    private void createTestLinks() {
        LinkInfo[] links = {
            createLink("PRIMARY", "121.41.10.128", null, "北京-东京", "2023-01-15 10:30:00", "2024-01-15 10:30:00", "299.99", "阿里云", "张三", 1),
            createLink("SECONDARY", "119.29.29.29", "106.52.138.33", "上海-首尔-洛杉矶", "2023-02-20 14:20:00", "2024-02-20 14:20:00", "199.99", "腾讯云", "李四", 2),
            createLink("PRIMARY", "114.114.114.114", null, "广州-新加坡", "2023-03-10 09:15:00", "2024-03-10 09:15:00", "399.99", "华为云", "王五", 1),
            createLink("SECONDARY", "8.8.8.8", "8.8.4.4", "香港-伦敦-纽约-多伦多", "2023-04-05 16:45:00", "2024-04-05 16:45:00", "249.99", "AWS", "赵六", 4),
            createLink("PRIMARY", "1.1.1.1", null, "深圳-法兰克福", "2023-05-12 11:30:00", "2024-05-12 11:30:00", "349.99", "Azure", "钱七", 1),
            createLink("SECONDARY", "208.67.222.222", "208.67.220.220", "台北-悉尼-圣何塞", "2023-06-18 20:00:00", "2024-06-18 20:00:00", "450.00", "GCP", "孙八", 3),
            createLink("PRIMARY", "45.76.65.217", null, "杭州-莫斯科", "2023-07-22 18:10:00", "2024-07-22 18:10:00", "188.88", "Vultr", "周九", 1),
            createLink("SECONDARY", "101.132.186.234", "159.65.130.227", "成都-孟买-迪拜", "2023-08-30 12:00:00", "2024-08-30 12:00:00", "520.00", "DigitalOcean", "吴十", 2)
        };
        for (LinkInfo link : links) {
            linkInfoService.save(link);
        }
    }

    private LinkInfo createLink(String linkType, String ipAddress, String ipAddress2, String regionPath, 
                                String purchaseTimeStr, String expireTimeStr, String price, 
                                String vpsProvider, String purchaser, int jumpCount) {
        LinkInfo link = new LinkInfo();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        link.setLinkType(linkType);
        link.setIpAddress(ipAddress);
        link.setIpAddress2(ipAddress2);
        link.setRegionPath(regionPath);
        link.setPurchaseTime(LocalDateTime.parse(purchaseTimeStr, formatter));
        link.setExpireTime(LocalDateTime.parse(expireTimeStr, formatter));
        link.setPrice(new BigDecimal(price));
        link.setVpsProvider(vpsProvider);
        link.setPurchaser(purchaser);
        link.setJumpCount(jumpCount);
        return link;
    }
} 