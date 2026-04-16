package com.yunshang.security.filter;

import com.alibaba.fastjson2.JSON;
import com.yunshang.common.jwt.JwtHelper;
import com.yunshang.common.result.Result;
import com.yunshang.common.result.ResultCodeEnum;
import com.yunshang.common.utils.ResponseUtil;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.security.custom.PermissionLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author nanfeng
 * @description 认证解析token的组件，每次访问接口的时候都需要进行token认证
 * @date 2023-03-14 20:58
 */
@SuppressWarnings("rawtypes")
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PermissionLoader permissionLoader;

    public TokenAuthenticationFilter(RedisTemplate<String, Object> redisTemplate, PermissionLoader permissionLoader) {
        this.redisTemplate = redisTemplate;
        this.permissionLoader = permissionLoader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("uri:" + request.getRequestURI());
        // 如果是登录接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:" + token);
        if (StringUtils.hasLength(token)) {
            String username = JwtHelper.getUsername(token);
            logger.info("username:" + username);
            if (StringUtils.hasLength(username)) {
                // 通过ThreadLocal记录当前登录人信息
                Long userId = JwtHelper.getUserId(token);
                LoginUserInfoHelper.setUserId(userId);
                LoginUserInfoHelper.setUsername(username);
                // 认证成功，从redis获取权限数据
                String authorities = (String) redisTemplate.opsForValue().get(username);
                List<SimpleGrantedAuthority> authorityList;
                if (StringUtils.hasLength(authorities)) {
                    // Redis命中，解析权限
                    authorityList = parseAuthorities(authorities);
                } else if (permissionLoader != null) {
                    // Redis未命中，降级从数据库加载权限并回写Redis
                    logger.info("Redis权限缓存未命中，从数据库加载，username:" + username);
                    List<String> permsList = permissionLoader.loadPermissions(userId);
                    authorityList = new ArrayList<>();
                    for (String perm : permsList) {
                        authorityList.add(new SimpleGrantedAuthority(perm.trim()));
                    }
                    // 回写Redis缓存
                    redisTemplate.opsForValue().set(username, JSON.toJSONString(authorityList));
                } else {
                    authorityList = Collections.emptyList();
                }
                return new UsernamePasswordAuthenticationToken(username, null, authorityList);
            }
        }
        return null;
    }

    /**
     * 解析Redis中存储的权限JSON
     */
    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> parseAuthorities(String authorities) {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        List<Map> maps = JSON.parseArray(authorities, Map.class);
        for (Map map : maps) {
            authorityList.add(new SimpleGrantedAuthority((String) map.get("authority")));
        }
        return authorityList;
    }
}
