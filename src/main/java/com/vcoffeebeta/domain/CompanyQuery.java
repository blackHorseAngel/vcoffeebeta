package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 公司查询实体类
 * @Author zhangshenming
 * @Date 2022/10/18 21:44
 * @Version 1.0.0
 */
@Setter
@Getter
@ToString
public class CompanyQuery extends Company implements Serializable {
    @Serial
    private static final long serialVersionUID = -2415006517155537942L;
    /**
     * 分页数据
     */
    private Page page;
}
