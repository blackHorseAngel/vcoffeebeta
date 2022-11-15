package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serializable;

/**
 * @Description 消费查询实体类
 * @Author zhangshenming
 * @Date 2022/10/18 21:45
 * @Version 1.0.0
 */
@Setter
@Getter
@ToString
public class ConsumeQuery extends Consume implements Serializable {
    /**
     * 分页数据
     */
    private Page page;
}
