package com.vcoffeebeta.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * @author zhangshenming
 * @date 2022/1/11 19:58
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class User implements Serializable {
    /**
     * 用户id
     */
    private long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户联系方式
     */
    private String telephoneNumber;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 是否是管理员用户
     */
    private byte isAdmin;
    /**
     * 用户所在公司id
     */
    private long companyId;
    /**
     * 用户账户id
     */
    private long accountId;
    /**
     * 用户常用设备id
     */
    private long equipId;
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
