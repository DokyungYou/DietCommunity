package com.example.dietcommunity.common.mail;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.MemberException;
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


  /**
   * 가입인증메일 전송 (접속 시 바로 인증처리되는 링크를 전송)
   */
  public void sendAuthEmail(String toEmail, String authCode) {

    log.debug("계정인증링크 이메일 전송: " + toEmail);
    try {
      javaMailSender.send(this.createAuthEmailMessage(toEmail, authCode));
    } catch (Exception e) {
      throw new MemberException(ErrorCode.FAIL_TO_SEND_MAIL);
    }

  }


  /**
   * 회원가입 인증 메세지 생성
   */
  private MimeMessage createAuthEmailMessage(String toEmail, String authCode) throws MessagingException {

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


    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    mimeMessage.setRecipients(RecipientType.TO, toEmail);
    mimeMessage.setFrom(username);
    mimeMessage.setSubject("DietCommunity 회원가입 인증");
    mimeMessage.setText(message, "utf-8", "html");

    return mimeMessage;
  }


}
