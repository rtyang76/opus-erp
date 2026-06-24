package com.opus.erp.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 认证由 JwtAuthInterceptor 处理，Spring Security 仅提供密码编码器
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器（使用 BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置
     * 所有请求放行，认证由 JwtAuthInterceptor 处理
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF
            .csrf(csrf -> csrf.disable())
            // 无状态 session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 所有请求放行（认证由 JwtAuthInterceptor 处理）
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // 禁用 form 登录
            .formLogin(form -> form.disable())
            // 禁用 httpBasic
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
