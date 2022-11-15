package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serializable;

/**
 * @Description 用户查询实体类
 * @Author zhangshenming
 * @Date 2022/10/18 21:49
 * @Version 1.0.0
 */
@Setter
@Getter
@ToString
public class UserQuery extends User implements Serializable {
    /**
     * 分页对象
     */
    private Page page;
}
