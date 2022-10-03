package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户实体类
 * @author zhangshenming
 * @date 2022/10/2 22:21
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class Account {
    /**
     * 主键id
     */
    private long id;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 账户余额
     */
    private BigDecimal remaining;
    /**
     * 账户状态
     */
    private byte state;
    /**
     * 创建人
     */
    private String created;
    /**
     * 修改人
     */
    private String modified;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;
    /**
     * 用户名称
     */
    private String username;

}
