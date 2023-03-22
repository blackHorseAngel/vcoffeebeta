package com.vcoffeebeta.util;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
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
//          Message message = createSimpleMessage(fromMail, toMail, emailMessage, session);
//          transport.sendMessage(message, message.getAllRecipients());
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
     * 创建邮箱发送简单消息
     * @param
     * @param
     * @param
     * @param
     * @return
     */
    public static void createSimpleMessage() throws MessagingException {
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

        } catch (GeneralSecurityException | AddressException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    /**
     * 发送带附件的邮件
     * @throws MessagingException
     */
    public static void createAttachFileMessage() throws MessagingException {
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
            //上传附件需要使用mimeMessageHelper对象
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true);
            //set from 头部字段
            mimeMessageHelper.setFrom(new InternetAddress(from));
            //set to 头部字段
            mimeMessageHelper.setTo(new InternetAddress(to));
            //set subject 头字段
            mimeMessageHelper.setSubject("test mail transporting");
            //设置消息体
            mimeMessageHelper.setText("set message successfully");
            //增加附件的地址
            mimeMessageHelper.addAttachment("test.jpg",new File("C:\\Users\\Administrator\\Desktop"));
            //发送邮件
            transport = session.getTransport();
//            transport.connect("smtp.qq.com",587,"714680900@qq.com","rfmcgesvyfgzbbhc");
            transport.connect("smtp.qq.com",465,from,"rfmcgesvyfgzbbhc");
            transport.send(message);

        } catch (GeneralSecurityException | AddressException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    /**
     * 发送多张带图片的邮件
     * @throws MessagingException
     */
    public static void createMorePicturesMessage() throws MessagingException {
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
            //上传附件需要使用mimeMessageHelper对象
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true);
            //set from 头部字段
            mimeMessageHelper.setFrom(new InternetAddress(from));
            //set to 头部字段
            mimeMessageHelper.setTo(new InternetAddress(to));
            //set subject 头字段
            mimeMessageHelper.setSubject("test mail transporting");
            //设置消息体
            mimeMessageHelper.setText("set message successfully");
            //增加图片资源
            mimeMessageHelper.addInline("p01",new FileSystemResource(new File("")));
            mimeMessageHelper.addInline("p02",new FileSystemResource(new File("")));
            //发送邮件
            transport = session.getTransport();
//            transport.connect("smtp.qq.com",587,"714680900@qq.com","rfmcgesvyfgzbbhc");
            transport.connect("smtp.qq.com",465,from,"rfmcgesvyfgzbbhc");
            transport.send(message);

        } catch (GeneralSecurityException | AddressException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }
    //引入freemarker（带表格）
    //引入thymleaf（带表格）
    /**
     * 邮箱发送测试/接受邮件测试
     * @param args
     */
    public static void main(String[] args) throws MessagingException {

    }
}
