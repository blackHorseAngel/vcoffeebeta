package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 公司实体类
 * @author zhangshenming
 * @date 2022/3/15 23:56
 * @version 1.0
 */
@Setter
@Getter
public class Company implements Serializable {
    /**
     * 公司id
     */
   private long id;
   /**
    *  公司名称
    */
   private String companyName;
    /**
     * 公司别名
     */
   private String companyNickName;
    /**
     * 公司联系方式
     */
   private String companyPhoneNumber;
    /**
     * 公司地址
     */
   private String companyAddress;
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
