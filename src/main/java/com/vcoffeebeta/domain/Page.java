package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页实体类
 * @author zhangshenming
 * @date 2022/4/16 22:25
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class Page {
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总记录数
     */
    private int totalCount;
    /**
     * 每页显示的记录
     */
    private int limit = 10;

}
