package me.hakyuwon.ecostep.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.EmailDto;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private static final String senderEmail = "weecostep@gmail.com";

    // 인증번호 저장을 위한 Map
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    // 랜덤으로 숫자 생성
    public String createNumber() {
        Random random = new Random();
        int number = random.nextInt(10000);
        return String.format("%04d", number); // 4자리 숫자
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>5분 안에 인증 번호를 입력해 주세요.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public String sendSimpleMessage(EmailDto.EmailRequestDto dto) throws MessagingException {
        String sendEmail = dto.getEmail();
        String number = createNumber(); // 랜덤 인증번호 생성

        try {
            MimeMessage message = createMail(sendEmail, number); // 메일 생성
            mailSender.send(message); // 메일 발송
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }
        // 인증번호 저장
        verificationCodes.put(sendEmail, number);
        return number;
    }

    public boolean verifyCode(EmailDto.VerifyCodeRequestDto dto) {
        String email = dto.getEmail();
        String inputCode = dto.getCode();

        String storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            verificationCodes.remove(email); // 인증번호 사용 후 삭제
            return true;
        }
        return false;
    }
}