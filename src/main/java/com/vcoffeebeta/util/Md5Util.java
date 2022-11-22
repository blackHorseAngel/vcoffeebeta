package com.vcoffeebeta.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
/**
 * Message Digest Algorithm MD5（中文名为消息摘要算法第五版）为计算机安全领域广泛使用的一种散列函数，用以提供消息的完整性保护。
 * MD5算法具有以下特点：
 * 1、压缩性：任意长度的数据，算出的MD5值长度都是固定的。
 * 2、容易计算：从原数据计算出MD5值很容易。
 * 3、抗修改性：对原数据进行任何改动，哪怕只修改1个字节，所得到的MD5值都有很大区别。
 * 4、强抗碰撞：已知原数据和其MD5值，想找到一个具有相同MD5值的数据（即伪造数据）是非常困难的。
 * 5、不可逆（作者自己加的）：已知原数据的MD5值，无法算出原数据。
 */

/**
 * @Description md5加密处理
 * @Author Administrator
 * @Date 2022/11/20 17:08
 * @Version 1.0.0
 */
@Slf4j
public class Md5Util {
    /**
     * 把原始字符串进行md5加密操作
     * @param str
     * @return
     */
    public static String getMd5String(String str){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            log.error("转换报错",e.getMessage());
            e.printStackTrace();
            return "";
        }
        char[]charArr = str.toCharArray();
        byte[]byteArr = new byte[charArr.length];
        for(int i = 0 ; i < charArr.length ; i++){
           byteArr[i] = (byte) charArr[i];
        }
        byte[]md5Bytes = md5.digest(byteArr);
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ; i < md5Bytes.length ; i++){
           int val = ((int)md5Bytes[i]) & 0xff;
           if(val < 16){
               buffer.append("0");
           }
           buffer.append(Integer.toHexString(val));
        }
        return buffer.toString();
    }

    /**
     * 比较传入的密码经过md5加密之后的字符串与数据库中的加过密的密码是否一致
     * @param password
     * @param md5Password
     * @return
     */
    public static boolean compareMd5Password(String password,String md5Password){
        String md5 = getMd5String(password);
        return md5.equals(md5Password) ? true : false;
    }

    /**
     * MD5加密测试
     * @param args
     */
    public static void main(String[] args) {
        String str = "123456";
        String md5Str = Md5Util.getMd5String(str);
        System.out.println("加密后的字符串是：" + md5Str);
        boolean flag = Md5Util.compareMd5Password(str,md5Str);
        System.out.println("新密码和经过MD5加密之后的密码是否相等" + flag);

    }
}
