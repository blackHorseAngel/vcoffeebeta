package com.vcoffeebeta.util;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

/**
 * @Description 邮箱登录工具类
 * 邮箱发送邮件的步骤：
 * 0、获取系统属性，设置邮件服务器，传输协议，密钥，
 * 1、创建session
 * 2、通过session得到transport对象
 * 3、使用邮箱的用户名和密码链接上的邮件服务器，发送邮件时，发送人需要提交邮箱的用户名和密码（授权码）给stmp服务器
 * 用户名和密码通过验证之后，才能正常发送邮件给收件人，QQ邮箱需要使用SSL，端口号默认是465或587，
 * 授权码不是qq密码
 * 我的qq邮箱714680900/zamzhongbei的授权码：rfmcgesvyfgzbbhc
 * 我的新浪邮箱zhangsm51的授权码：e87e114c8522e5e7
 * 我的新浪邮箱zhangsm_51的授权码：5f81da7d4383079e
 * 我的网易163邮箱授权码：AUBBRSDZRGKQFFHO
 *
 *  对于QQ邮箱，还需要进行SSL加密
 *             MailSSLSocketFactory sf = new MailSSLSocketFactory();
 *             sf.setTrustAllHosts(true);
 *             properties.put("mail.smtp.ssl.enable","true");
 *             properties.put("mail.smtp.ssl.socketFactory",sf);
 *
 * 4、创建邮件MimeMessage对象，上传附件需要使用mimeMessageHelper对象，设置发件人邮箱from，收件人邮箱to，主题subject，消息体，附件地址/图片,
 *   抄送人toCc，密送人toBcc，优先级（1：紧急，3：普通，5：低）
 * 5、通过session获取transport对象，发送邮件
 * @Author Administrator
 * @Date 2022/11/22 10:06
 * @Version 1.0.0
 */
@Slf4j
public class MailUtil {

    @Autowired
    TemplateEngine templateEngine;

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

    /**
     * 引入thymleaf（带表格）
     * @author zhangshenming
     * @date
     * @param
     *
     */
    public void createThymeleafMail() throws MessagingException {
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
            mimeMessageHelper.setSubject("test thymeleaf mail transporting");
            //设置消息体
            mimeMessageHelper.setText("set message successfully");
            //设置发送日期
            mimeMessageHelper.setSentDate(new Date());
            Context context = new Context();
            context.setVariable("username","Sam");
            context.setVariable("num","101010");
            context.setVariable("salary","10000");
            String process = templateEngine.process("../email/example.html",context);
            mimeMessageHelper.setText(process,true);
            //增加图片资源
//            mimeMessageHelper.addInline("p01",new FileSystemResource(new File("")));
//            mimeMessageHelper.addInline("p02",new FileSystemResource(new File("")));
            //发送邮件
            transport = session.getTransport();
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
     * 接收邮件
     * @auyhor
     * @date
     * @params
     */
    public static void receiveMails(){
        //接收方的传输协议
        String protocol = "pop3";
        //接收方的主机地址
        String receiveHost =  "pop.qq.com";
        //接收方名称
        String receiveUsername = "1909924230@qq.com";
        //接收方的名称对应的授权码
        String receivePassword = "";
        //设置属性
        Properties properties = new Properties();
        //设置传输协议
        properties.setProperty("mail.transport.protocol",protocol);
        //设置收件人的smtp的主机地址
        properties.setProperty("mail.smtp.host",receiveHost);
        //获取连接
        Session session = Session.getDefaultInstance(properties);
        //debug模式打开
        session.setDebug(true);
        Store store = null;
        Folder folder = null;
        try {
             store = session.getStore(protocol);
             store.connect(receiveHost,receiveUsername,receivePassword);
            //通过POP3协议获取收件人的邮件账户，指定邮件夹的名称，只能是INBOX
            folder = store.getFolder("INBOX");
            //设置对邮件用户的访问权限
            folder.open(Folder.READ_ONLY);
            //获取收件人的邮箱中的全部邮件
            Message[]messages = folder.getMessages();
            for(Message message:messages){
                String subject = message.getSubject();
                Address from = (Address) message.getFrom()[0];
                System.out.println("发件人是：" + from);
                System.out.println("邮件主题是：" + subject);
                message.writeTo(System.out);
            }
            folder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 邮箱发送测试/接受邮件测试
     * @param args
     */
    public static void main(String[] args) throws MessagingException {
        //接收方的传输协议
        String protocol = "pop3";
        //接收方的主机地址
        String receiveHost =  "pop.qq.com";
        //接收方名称
        String receiveUsername = "714680900@qq.com";
        //接收方的名称对应的授权码
        String receivePassword = "rfmcgesvyfgzbbhc";
        //设置属性
        Properties properties = new Properties();
        //设置传输协议
        properties.setProperty("mail.transport.protocol",protocol);
        //设置收件人的smtp的主机地址
        properties.setProperty("mail.smtp.host",receiveHost);
        //获取连接
        Session session = Session.getDefaultInstance(properties);
        //debug模式打开
        session.setDebug(true);
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(receiveHost,receiveUsername,receivePassword);
            //通过POP3协议获取收件人的邮件账户，指定邮件夹的名称，只能是INBOX
            folder = store.getFolder("INBOX");
            //设置对邮件用户的访问权限
            folder.open(Folder.READ_ONLY);
            //获取收件人的邮箱中的全部邮件
            Message[]messages = folder.getMessages();
            for(Message message:messages){
                String subject = message.getSubject();
                Address from = (Address) message.getFrom()[0];
                System.out.println("发件人是：" + from);
                System.out.println("邮件主题是：" + subject);
                message.writeTo(System.out);
            }
            folder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
