package com.sparta.seoulmate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.seoulmate.config.EmbeddedRedisConfig;
import com.sparta.seoulmate.dto.test.SignupRequestTestDto;
import com.sparta.seoulmate.dto.user.LoginRequestDto;
import com.sparta.seoulmate.entity.UserGenderEnum;
import com.sparta.seoulmate.entity.redishash.EmailVerification;
import com.sparta.seoulmate.entity.redishash.SmsVerification;
import com.sparta.seoulmate.jwt.JwtUtil;
import com.sparta.seoulmate.repository.EmailVerificationRepository;
import com.sparta.seoulmate.repository.SmsVerificationRepository;
import com.sparta.seoulmate.repository.UserRepository;
import com.sparta.seoulmate.service.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.Redis;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailVerificationRepository emailVerificationRepository;
    @Autowired
    SmsVerificationRepository smsVerificationRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    JwtUtil jwtUtil;
//    @Autowired
//    EmbeddedRedisConfig embeddedRedisConfig;

    String TEST_USER = "TEST_USER1";
    String TEST_PASSWORD = "TEST_PASSWORD1";
    String TEST_NICKNAME = "TEST_NICKNAME1";
    String TEST_PHONE = "0101234567";
    String TEST_EMAIL = "test3@example.com";
    String TEST_CODE = "test3@example.com";


//    @Test
    @DisplayName("이메일 인증코드 전송 및 확인 테스트")
    @Order(1)
    public void emailVerification() {
        // 이메일은 인증 되었다고 가정
        EmailVerification emailVerification = new EmailVerification(TEST_EMAIL, TEST_CODE);
        emailVerification.setVerificated();
        emailVerificationRepository.save(emailVerification);
    }

//    @Test
    @DisplayName("이메일 인증 확인 테스트")
    @Order(2)
    public void smsVerification() {
        // 휴대폰 인증 되었다고 가정
        SmsVerification smsVerification = new SmsVerification(TEST_PHONE, TEST_CODE);
        smsVerification.setVerificated();
        smsVerificationRepository.save(smsVerification);
    }


//    @Test
    @DisplayName("회원가입")
    @Order(3)
    public void signup() throws Exception {

        SignupRequestTestDto signupRequestDto = SignupRequestTestDto.builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .phone(TEST_PHONE)
                .age(25)
                .address("123 Main St")
                .gender(UserGenderEnum.MALE)
                .build();

        try{
            mockMvc.perform(post("/api/users/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequestDto)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


    }

//    @Test
    @DisplayName("로그인")
    @Order(4)
    public void login() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto
                .builder()
                .username(TEST_USER)
                .password(TEST_PASSWORD)
                .build();
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
