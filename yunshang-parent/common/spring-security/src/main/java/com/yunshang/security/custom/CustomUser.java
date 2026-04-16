package com.yunshang.security.custom;

import com.yunshang.model.system.SysUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-14 20:25
 */
@Setter
@Getter
public class CustomUser extends User {

    private SysUser sysUser;

    public CustomUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities) {
        super(sysUser.getUsername(), sysUser.getPassword(), authorities);
        System.out.println(sysUser.getPassword());
        this.sysUser = sysUser;
    }
}
