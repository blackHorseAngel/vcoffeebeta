package com.vcoffeebeta.enums;

/**
 * 返回结果码枚举类
 * @author zhangshenming
 * @version 1.0
 * @create 2022-03-10 10-12-18
 */
public enum ResultCodeEnum {
    /**
     *
     */
    SUCCESS("成功",1000),
    /**
     *
     */
    NONEXIST("用户名不存在或用户名错误",1001),
    /**
     *
     */
    PASSWORDERROR("用户密码错误",1002),
    /**
     *
     */
    USERNAMEEXIST("用户名已存在",1003),
    /**
     *
     */
    PHONENUMBERERROR("用户联系方式格式不正确",1004),
    /**
     *
     */
    EMAILERROR("用户邮箱格式不正确",1005),
    /**
     *
     */
    PASSWORDCONFIRMERROR("两次密码不一致",1006),
    /**
     *
     */
    INSERTUSERERROR("新增用户出错",1007),
    /**
     *
     */
    COMPANYEXIST("公司名称已存在",1008),
    /**
     *
     */
    COMPANYPHONENUMBERERROR("公司座机号格式错误",1009),
    /**
     *
     */
    INSERTCOMPANYERROR("新增公司报错",1010),
    /**
     *
     */
    QUERYCOMPANYERROR("根据条件查询公司报错",1011),
    /**
     *
     */
    UPDATECOMPANYERROR("更新公司信息报错",1012),
    /**
     *
     */
    TOUPDATECOMPANYERROR("跳转到更新公司页面报错",1013),
    /**
     *
     */
    DELETECOMPANYERROR("删除单个公司信息报错",1014),
    /**
     *
     */
    BATCHDELETECOMPANY("批量删除公司信息报错",1015),
    /**
     *
     */
    QUERYCOMPANYNAME("查询全部公司名称报错",1016),
    /**
     *
     */
    QUERYALLCOMPANYERROR("查询全部公司报错",1018),
    /**
     *
     */
    QUERYCOMPANYPAGEERROR("查询公司分页信息报错",1017),

    /**
     *
     */
    INSERTEQUIPMENTERROR("新增设备报错",2010),
    /**
     *
     */
    QUERYEQUIPMENTERROR("查询全部设备信息报错",2011),
    /**
     *
     */
    TOUPDATEEQUIPMENTERROR("跳转到更新设备页面报错",2012),
    /**
     *
     */
    UPDATEEQUIPMENTERROR("更新设备信息报错",2013),
    /**
     *
     */
    DELETEEQUIPMENTERROR("删除设备信息报错",2014),
    /**
     *  
     */
    BATCHDELETEEQUIPMENTERROR("批量删除设备信息报错",2015),
    /**
     *
     */
    QUERYEQUIPMENTBYOPTIONERROR("条件查询设备信息报错",2016),
    /**
     *
     */
    QUERYEQUIPMENTPAGEERROR("查询设备分页报错",2017),
    /**
     *
     */
    QUERYALLUSERS("查询全部用户信息报错",3010),
    /**
     *
     */
    SESSIONFORCOMPANYID("session信息中缺少companyId",3011),
    /**
     *
     */
    TOUPDATEUSER("跳转到更新用户页面报错",3012),
    /**
     *
     */
    UPDATEUSER("更新用户信息报错",3013),
    /**
     *
     */
    DELETEUSER("删除用户信息报错",3014),
    /**
     *
     */
    BATCHDELETEUSER("批量删除用户报错",3015),
    /**
     *
     */
    USERCOMPANYERROR("根据用户得到的公司id为空",3016),
    /**
     *
     */
    USEREQUIPMENTERROR("根据用户得到的设备信息为空",3017),
    /**
     *
     */
    QUERYUSERERROR("查询用户信息报错",3018),
    /**
     *
     */
    INSERTUSERCOMPANYDELETION("新增用户时公司已被删除",3019),
    /**
     *
     */
    QUERYUSERPAGEERROR("查询用户分页数据报错",3019),
    /**
     *
     */
    QUERYACCOUNTDETAILERROR("查询账户信息报错",4010),
    /**
     *
     */
    INSERTACCOUNTERROR("新增账户报错",4011),
    /**
     *
     */
    DELETEACCOUNTERROR("删除账户报错",4012),
    /**
     *
     */
    INTERNALERROR("服务器报错",500),
    /**
     *
     */
    SESSIONERROR("服务器session数据丢失",1500);
    /**
     * 返回码信息
     */
    private  String message;
    /**
     * 返回码
     */
    private int code;


    ResultCodeEnum(String message,int code) {
        this.message = message;
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    /**
     * 拿到返回码信息
     * @author zhangshenming
     * @date 2022/3/10 10:22
     * @param code
     * @return com.vcoffeebeta.enums.ResultCodeEnum
     */
    public ResultCodeEnum getResultCodeMessage(int code){
        for(ResultCodeEnum codeEnum:ResultCodeEnum.values()){
            if(codeEnum.getCode() == code){
                return ResultCodeEnum.valueOf(codeEnum.getMessage());
            }
        }
        return null;
    }
}
