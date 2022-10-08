package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 消费实体类
 * @author zhangshenming
 * @date 2022/10/3 23:11
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class Consume {
    /**
     * 主键id
     */
    private long id;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 账户id
     */
    private long accountId;
    /**
     * 操作类型
     */
    private byte type;
    /**
     * 操作金额
     */
    private BigDecimal amount;
    /**
     * 操作日期
     */
    private Date operateDate;
    /**
     * 操作者
     */
    private String operator;
    /**
     * 每个人的消费记录数
     */
    private String consumeNumber;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 页码
     */
    private Page page;
}
