package com.yunshang.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色查询实体
 */
@Data
public class SysRoleQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String roleName;
}

