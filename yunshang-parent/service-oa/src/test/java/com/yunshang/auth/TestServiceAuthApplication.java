package com.yunshang.auth;

import com.yunshang.auth.mapper.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-06 14:27
 */
@SpringBootTest
public class TestServiceAuthApplication {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Test
    public void testMpDemo1() {
        sysRoleMapper.selectList(null).forEach(System.out::println);
    }
}
