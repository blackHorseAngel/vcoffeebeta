package com.vcoffeebeta.util;
/**
 * 验证数据
 * @author zhangshenming
 * @date 2022/3/11 14:48
 * @version 1.0
 */
public class Validation {
    /**
     * 验证手机号
     * @author zhangshenming
     * @date 2022/3/11 14:49
     * @param telephoneNumber
     * @return boolean
     */
    public static boolean validateTelephoneNumber(String telephoneNumber){
        String regex = "^1[3-9]\\d{9}$";
        return telephoneNumber != null && telephoneNumber.matches(regex);
    }
    /**
     * 验证座机号
     * @author zhangshenming
     * @date 2022/3/16 22:10
     * @param phoneNumber
     * @return boolean
     */
    public static boolean validatePhoneNumber(String phoneNumber){
        String regex = "\\d{3,4}\\d{8}";
        return phoneNumber != null && phoneNumber.matches(regex);
    }
    /**
     * 电子邮箱验证
     * @author zhangshenming
     * @date 2022/3/11 14:59
     * @param email
     * @return boolean
     */
    public static boolean validateEmail(String email){
        String regex = "[\\w]+@[\\w]+.[\\w]+";
        return email != null && email.matches(regex);
    }
    /**
     * 比较两次密码是否一致
     * @author zhangshenming
     * @date 2022/3/11 18:51
     * @param password,confirmPassword
     * @return boolean
     */
    public static boolean validatePassword(String password,String confirmPassword){
        if(password.equals(confirmPassword)){
            return true;
        }
        return false;
    }

}
