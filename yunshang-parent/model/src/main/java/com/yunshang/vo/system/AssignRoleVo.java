package com.yunshang.vo.system;


import lombok.Data;

import java.util.List;

@Data
public class AssignRoleVo {

    private Long userId;

    private List<Long> roleIdList;

}
