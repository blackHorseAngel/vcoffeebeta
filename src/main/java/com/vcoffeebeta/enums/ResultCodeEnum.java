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
    NONEXIT("用户名不存在或用户名错误",1001),
    /**
     *
     */
    PASSWORDERROR("用户密码错误",1002),
    /**
     *
     */
    USERNAMEEXIT("用户名已存在",1003),
    /**
     *
     */
    PHONENUMBERERROR("用户联系方式不正确",1004),
    /**
     *
     */
    EMAILERROR("用户邮箱不正确",1005),
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
    INTERNALERROR("服务器报错",500);
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
