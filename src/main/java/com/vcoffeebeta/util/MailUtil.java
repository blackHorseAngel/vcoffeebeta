package com.vcoffeebeta.util;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
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
      prop.setProperty("mail.host","smtp.qq.com");
      prop.setProperty("mail.transport.protocol","smtp");
      prop.setProperty("mail.smtp.auth","true");
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
//        boolean flag = sendEmail("1909924230","714680900","该用户已激活");
//        System.out.println(flag);
        //收件人邮箱
        String to = "1909924230@qq.com";
        //发送人邮箱
        String from = "714680900@qq.com";
        //发送邮箱主机
        String host = "smtp.qq.com";
        //获取系统属性
        Properties properties = System.getProperties();
        System.out.println("获取到的系统属性：" + properties.toString());
        //设置邮件服务器
        properties.setProperty("mail.smtp.host",host);
        //设置传输协议
        properties.setProperty("mail.transport.protocol","smtp");
        //设置密钥
        properties.setProperty("mail.smtp.auth","true");
        //设置端口号为587
        properties.setProperty("mail.smtp.socketFactory.port","587");

        Transport transport = null;
        try {
            //对于QQ邮箱，还需要进行SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable","true");
            properties.put("mail.smtp.ssl.socketFactory",sf);
//            properties.put("mail.smtp.ssl.protocols","TLSv1.2");
            //获取默认的session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from,"rfmcgesvyfgzbbhc");
                }
            });
            session.setDebug(true);
            //创建默认的MimeMessage对象
            MimeMessage message = new MimeMessage(session);
            //set from 头部字段
            message.setFrom(new InternetAddress(from));
            //set to 头部字段
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            //set subject 头字段
            message.setSubject("test mail transporting");
            //设置消息体
            message.setText("set message successfully");
            //发送邮件
            transport = session.getTransport();
//            transport.connect("smtp.qq.com",587,"714680900@qq.com","rfmcgesvyfgzbbhc");
            transport.connect("smtp.qq.com",465,from,"rfmcgesvyfgzbbhc");
            transport.send(message);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }finally {
            transport.close();
        }

    }
}
