package com.vcoffeebeta.domain;

import lombok.*;

import java.util.Date;

/**
 * 设备实体类
 * @author zhangshenming
 * @date 2022/9/21 10:38
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class Equipment {
    /**
     * 主键id
     */
    private long id;
    /**
     *  设备序列号
     */
    private String serialNumber;
    /**
     * 设备名称
     */
    private String equipmentName;
    /**
     * 设备是否投入使用
     */
    private byte isUsed;
    /**
     * 设备归属公司
     */
    private long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 维修人员名称
     */
    private String attendantName;
    /**
     * 维修人员电话
     */
    private String attendantTelephoneNumber;
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

}
