package com.vcoffeebeta.domain;

import lombok.*;

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
    /**
     * 分页数据
     */
    private Page page;
}
