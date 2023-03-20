package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 账户实体类
 * @author zhangshenming
 * @date 2022/10/2 22:21
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = -6727119777586853345L;
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
     * 用户编号
     */
    private String userNumber;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 设备名称
     */
    private String equipmentName;

}
