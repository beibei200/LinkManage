package com.beibei.linkmanagement.config;

import com.beibei.linkmanagement.entity.SysUser;
import com.beibei.linkmanagement.service.SysUserService;
import com.beibei.linkmanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter implements ApplicationContextAware {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtUtil jwtUtil;
    private ApplicationContext applicationContext;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.debug("处理请求: {}", requestURI);
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            logger.debug("找到JWT token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            if (jwtUtil.validateToken(token)) {
                logger.debug("JWT token验证成功");
                
                String username = jwtUtil.getUsernameFromToken(token);
                logger.debug("从token中提取用户名: {}", username);
                
                // 通过ApplicationContext获取SysUserService，避免循环依赖
                SysUserService sysUserService = applicationContext.getBean(SysUserService.class);
                SysUser user = sysUserService.getUserByUsername(username);
                
                if (user != null) {
                    String role = "ROLE_" + user.getRole();
                    logger.debug("用户角色: {}", role);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("认证信息已设置到SecurityContext");
                } else {
                    logger.warn("用户不存在: {}", username);
                }
            } else {
                logger.warn("JWT token验证失败");
            }
        } else {
            logger.debug("请求中没有JWT token");
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 