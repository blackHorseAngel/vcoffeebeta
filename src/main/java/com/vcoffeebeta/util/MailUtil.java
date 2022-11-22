package com.vcoffeebeta.util;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Description 邮箱登录工具类
 * @Author Administrator
 * @Date 2022/11/22 10:06
 * @Version 1.0.0
 */
@Slf4j
public class MailUtil {
  public static boolean sendEmail(String fromMail,String toMail,String emailMessage) throws MessagingException {
      //1、创建session
      Properties prop = new Properties();
      prop.setProperty("mail.host","stmp.qq.com");
      prop.setProperty("mail.transport.protocol","stmp");
      prop.setProperty("mail.stmp.auth","true");
      Session session = Session.getInstance(prop);
      //2、通过session得到transport对象
      Transport transport = null;
      try {
          transport = session.getTransport();
          //3、使用邮箱的用户名和密码链接上的邮件服务器，发送邮件时，发送人需要提交邮箱的用户名和密码（授权码）给stmp服务器
          //用户名和密码通过验证之后，才能正常发送邮件给收件人，QQ邮箱需要使用SSL，端口号默认是465或587，
          //授权码不是qq密码
          //我的qq邮箱714680900/zamzhongbei的授权码：rfmcgesvyfgzbbhc
          //我的新浪邮箱zhangsm51的授权码：e87e114c8522e5e7
          //我的新浪邮箱zhangsm_51的授权码：5f81da7d4383079e
      transport.connect("stmp.qq.com",587,"714680900","rfmcgesvyfgzbbhc");
//          transport.connect("714680900", "rfmcgesvyfgzbbhc");
          //4、创建邮件
          Message message = createSimpleMessage(fromMail, toMail, emailMessage, session);
          transport.sendMessage(message, message.getAllRecipients());
      }catch(NoSuchProviderException e1){
          log.error("没有获取到对应的邮箱服务器异常",e1);
          e1.printStackTrace();
          return false;
      }catch(MessagingException e2){
         log.error("消息异常",e2);
          e2.printStackTrace();
          return false;
      }catch (Exception e){
         log.error("其他未能捕获的异常信息",e);
         e.printStackTrace();
         return false;
      }finally {
          transport.close();
      }
      return true;
  }

    /**
     * 创建邮箱发送消息
     * @param fromMail
     * @param toMail
     * @param emailMessage
     * @param session
     * @return
     */
    private static Message createSimpleMessage(String fromMail, String toMail, String emailMessage, Session session) {
        //创建邮件
        MimeMessage message = new MimeMessage(session);
        try{
            //指明邮件的发送人
            message.setFrom(new InternetAddress(fromMail));
            //指明邮件的收件人
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(toMail));
            //设置邮件标题
            message.setSubject("用户激活");
            //设置邮件内容
            message.setContent(emailMessage,"text/html;charset=utf-8");
        }catch(AddressException e1){
            log.error("地址异常",e1);
            e1.printStackTrace();
        }catch(MessagingException e2){
            log.error("消息异常",e2);
            e2.printStackTrace();
        }
      return message;
    }

    /**
     * 邮箱发送测试
     * @param args
     */
    public static void main(String[] args) throws MessagingException {
        boolean flag = sendEmail("1909924230","714680900","该用户已激活");
        System.out.println(flag);
    }
}
