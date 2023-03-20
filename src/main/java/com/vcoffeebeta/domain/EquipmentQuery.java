package com.vcoffeebeta.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 设备查询实体类
 * @Author zhangshenming
 * @Date 2022/10/18 21:47
 * @Version 1.0.0
 */
@Setter
@Getter
@ToString
public class EquipmentQuery extends Equipment implements Serializable {
    @Serial
    private static final long serialVersionUID = 7009805260670882372L;
    /**
     * 分页对象
     */
    private Page page;
}
