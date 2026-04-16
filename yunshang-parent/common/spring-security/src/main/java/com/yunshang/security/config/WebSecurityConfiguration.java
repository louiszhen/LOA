package com.yunshang.security.config;

import com.yunshang.security.custom.PermissionLoader;
import com.yunshang.security.filter.TokenAuthenticationFilter;
import com.yunshang.security.filter.TokenLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description SpringSecurity配置类
 * @date 2023-03-14 09:18
 */
@Configuration
public class WebSecurityConfiguration {

    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "permissionLoader")
    @Lazy
    private PermissionLoader permissionLoader;

    /**
     * 获取AuthenticationManager（认证管理器），登录时认证使用
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // 基于token，不需要csrf，关闭csrf跨站请求伪造
                .csrf().disable()
                // 开启跨域以便前端调用接口
                .cors().and()
                // 基于token，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 指定登录接口不需要通过验证即可访问，其他接口需要进行验证才能访问
                .authorizeRequests()
                .antMatchers("/admin/system/index/login").permitAll()
                .antMatchers("/admin/meeting/**").permitAll()
                .anyRequest().authenticated().and()
                // TokenAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面，
                // 这样做就是为了除了登录的时候去查询数据库外，其他时候都用token进行认证
                .addFilterBefore(new TokenAuthenticationFilter(redisTemplate, permissionLoader), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new TokenLoginFilter(authenticationManager, redisTemplate))
                .build();
    }

    /**
     * 配置哪些请求不拦截
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/favicon.ico", "/swagger-resources/**",
                "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/doc.html", "/admin/wechat/authorize",
                "/admin/wechat/userInfo", "/admin/wechat/bindPhone");
    }
}
