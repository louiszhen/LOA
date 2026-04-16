package com.yunshang.vo.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户VO对象，包含部门名称和角色名称
 *
 * @author louiszhen
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserVo extends com.yunshang.model.system.SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色名称，多个角色用逗号分隔
     */
    private String roleName;
}
