package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

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
    @Serial
    private static final long serialVersionUID = 8534998798010493090L;
    /**
     * 分页对象
     */
    private Page page;

    private String createdTimeStr;

    private String modifiedTimeStr;
}
