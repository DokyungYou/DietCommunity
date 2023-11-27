package com.example.dietcommunity.common.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;



  @Bean
  public JavaMailSender JavaMailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    javaMailSender.setHost(this.host);
    javaMailSender.setPort(this.port);
    javaMailSender.setUsername(this.username);
    javaMailSender.setPassword(this.password);

    javaMailSender.setJavaMailProperties(getMaliProperties());
    javaMailSender.setDefaultEncoding("UTF-8");


    return javaMailSender;
  }

  private Properties getMaliProperties(){
    Properties properties = new Properties();
    properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜 설정
    properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
    properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp starttls 사용
    properties.setProperty("mail.debug", "true");
    properties.setProperty("mail.smtp.ssl.trust","smtp.naver.com"); // ssl 인증 서버 주소
    properties.setProperty("mail.smtp.ssl.enable","true");
    return properties;
  }

}
