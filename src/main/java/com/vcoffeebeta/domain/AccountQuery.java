package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 账户查询类
 * @Author zhangshenming
 * @Date 2022/10/18 21:41
 * @Version 1.0.0
 */
@Setter
@Getter
@ToString
public class AccountQuery extends Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 9053799046712612690L;
    /**
     * 分页数据
     */
    private Page page;
}
