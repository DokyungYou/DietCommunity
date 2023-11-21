package com.example.dietcommunity.common.mail;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.MailException;
import com.example.dietcommunity.common.exception.MemberException;
import java.util.concurrent.ThreadLocalRandom;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String username;


  private void sendMimeMessage(String toEmail, String subject ,String message) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      mimeMessage.setRecipients(RecipientType.TO, toEmail);
      mimeMessage.setFrom(username);
      mimeMessage.setSubject(subject);
      mimeMessage.setText(message, "utf-8", "html");

      javaMailSender.send(mimeMessage);
    } catch (Exception e) {
      throw new MailException(ErrorCode.FAIL_TO_SEND_MAIL);
    }
  }




  /**
   * 가입인증메일 전송 (접속 시 바로 인증처리되는 링크를 전송)
   */
  public void sendAuthEmailMessage(String toEmail, String authCode){

    String linkAddress = "http://localhost:8080/member/authenticate-email?email=" + toEmail + "&authCode=" + authCode;

    String message = "\n"
        + "<!DOCTYPE html>\n"
        + "<html lang=\"en\">\n"
        + " <head>\n"
        + " <meta charset=\"UTF-8\" />\n"
        + " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
        + " <title>DietaryCommunity</title>\n"
        + " </head>\n"
        + " <body>\n"
        + " <h1> 안녕하세요 DietaryCommunity 입니다. </h1>\n"
        + " <h3> 이메일 인증을 완료하려면 아래 링크를 클릭하세요. </h3>\n"
        + " <div style=\"align-self: center; border: 5px solid black; width: 50%; height: 10%;\">\n"
        + "<a href=\"" + linkAddress + "\">계정 활성화하기</a>"
        + " </body>\n"
        + "</html>";

    sendMimeMessage(toEmail,"DietCommunity 회원가입 인증", message);
  }


  /**
   * 이메일로 계정 아이디 전송
   * @param toEmail
   * @param accountId
   */
  public void sendFindAccountIdMessage(String toEmail, String accountId) {

    String message = "\n"
        + "<!DOCTYPE html>\n"
        + "<html lang=\"en\">\n"
        + " <head>\n"
        + " <meta charset=\"UTF-8\" />\n"
        + " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
        + " <title>DietaryCommunity</title>\n"
        + " </head>\n"
        + " <body>\n"
        + " <h1> 안녕하세요 DietaryCommunity 입니다. </h1>\n"
        + " <h3> 해당 이메일로 가입한 아이디는 " +  accountId  + "입니다.</h3>\n"
        + " <div style=\"align-self: center; border: 5px solid black; width: 50%; height: 10%;\">\n"
        + " </body>\n"
        + "</html>";

    sendMimeMessage(toEmail,"DietCommunity 아이디 찾기" , message);
  }


  /**
   * 임시비밀번호 메세지 발송
   * @param toEmail
   * @param temporaryPassword
   */
  public void sendFindPasswordMessage(String toEmail, String temporaryPassword) {

    String message = "\n"
        + "<!DOCTYPE html>\n"
        + "<html lang=\"en\">\n"
        + " <head>\n"
        + " <meta charset=\"UTF-8\" />\n"
        + " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
        + " <title>DietaryCommunity</title>\n"
        + " </head>\n"
        + " <body>\n"
        + " <h1> 안녕하세요 DietaryCommunity 입니다. </h1>\n"
        + " <p> 임시비밀번호는" + temporaryPassword + "입니다. 해당 비밀번호로 로그인 후 비밀번호를 재설정하시길바랍니다. </p>\n"
        + " <div style=\"align-self: center; border: 5px solid black; width: 50%; height: 10%;\">\n"
        + " </body>\n"
        + "</html>";

    sendMimeMessage(toEmail,"DietCommunity 비밀번호 찾기" ,message);

  }


}
