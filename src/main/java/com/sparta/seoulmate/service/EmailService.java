package com.sparta.seoulmate.service;

import com.sparta.seoulmate.entity.redishash.EmailVerification;
import com.sparta.seoulmate.repository.EmailVerificationRepository;
import com.sparta.seoulmate.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailService {


    private final JavaMailSender emailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    private String ePw;

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("SEOUL MATE 회원가입 이메일 인증");

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> SEOULMATE 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("gusrb1502@gmail.com", "SEOULMATE"));

        return message;
    }

    public String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    @Transactional
    public String sendSimpleMessage(String email) throws Exception {
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("이미 가입한 이메일입니다.");
        userRepository.deleteById(4L);
        ePw = createKey();

        MimeMessage message = createMessage(email);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        Optional<EmailVerification> emailVerification = emailVerificationRepository.findById(email);
        emailVerification.ifPresent(emailVerificationRepository::delete);
        emailVerificationRepository.save(new EmailVerification(email, ePw));
        return ePw;
    }

    public void mailVerification(String email, String code) {

        EmailVerification emailVerification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("인증코드를 발송해주세요."));

        if (emailVerification.getCode().equals(code)) {
            emailVerification.setVerificated();
            emailVerificationRepository.save(emailVerification);
        } else throw new IllegalArgumentException("인증에 실패했습니다.");
    }
}
