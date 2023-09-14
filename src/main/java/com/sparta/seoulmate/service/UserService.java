package com.sparta.seoulmate.service;

import com.sparta.seoulmate.config.FileComponent;
import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.dto.user.UserResponseDto;
import com.sparta.seoulmate.dto.user.*;
import com.sparta.seoulmate.entity.*;
import com.sparta.seoulmate.entity.redishash.Blacklist;
import com.sparta.seoulmate.entity.redishash.EmailVerification;
import com.sparta.seoulmate.entity.redishash.RefreshToken;
import com.sparta.seoulmate.entity.redishash.SmsVerification;
import com.sparta.seoulmate.jwt.JwtUtil;
import com.sparta.seoulmate.repository.*;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final SmsVerificationRepository smsVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistRepository blacklistRepository;
    private final JwtUtil jwtUtil;
    private final PasswordManagerRepository passwordManagerRepository;
    private final FileComponent fileComponent;
    private final ImageRepository imageRepository;
    private final WithdrawalRepository withdrawalRepository;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signUp(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        String phone = requestDto.getPhone();
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        EmailVerification emailVerification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증 코드를 발송하지 않았습니다."));
        if (!emailVerification.isVerificated()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }
        SmsVerification smsVerification = smsVerificationRepository.findById(phone)
                .orElseThrow(() -> new IllegalArgumentException("전화번호 인증 코드를 발송하지 않았습니다."));
        if (!smsVerification.isVerificated()) {
            throw new IllegalArgumentException("전화번호 인증이 완료되지 않았습니다.");
        }
        // 회원 아이디 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = requestDto.toEntity(role, password);
        userRepository.save(user);

        // 회원가입 시 썼던 비밀번호를 PasswordManager 엔티티에 저장
        PasswordManager passwordManager = PasswordManager.builder()
                .user(user)
                .password(password)    // 업데이트된 비밀번호
                .build();
        passwordManagerRepository.save(passwordManager);
    }

    public void logout(User user, HttpServletRequest request) {
        String refreshTokenVal = request.getHeader("RefreshToken").substring(7);
        String accessTokenVal = jwtUtil.getJwtFromHeader(request);

        try {
            Key key = jwtUtil.getKey();
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessTokenVal)
                    .getBody()
                    .getExpiration();
            long expireTime = expiration.getTime() - System.currentTimeMillis();
            blacklistRepository.save(new Blacklist(accessTokenVal, expireTime / 1000));
            RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenVal)
                    .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 없습니다."));
            refreshTokenRepository.delete(refreshToken);
            System.out.println(expiration.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 프로필 수정(닉네임)
    @Transactional
    public void updateNickname(UpdateNicknameRequestDto requestDto, User user) {
        User targetUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        if (!targetUser.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 중복 확인 (새로운 닉네임에 대한 중복 확인)
        Optional<User> checkNickname = userRepository.findByNickname(requestDto.getNickname());
        if (checkNickname.isPresent() && !checkNickname.get().equals(targetUser)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임 입니다.");
        }

        targetUser.updateNickname(requestDto.getNickname());
    }

    // 프로필 수정(주소)
    @Transactional
    public void updateAddress(UpdateAddressRequestDto requestDto, User user) {
        User targetUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        targetUser.updateAddress(requestDto.getAddress());
    }

    // 프로필 조회
    public UserResponseDto getUserProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 ID를 찾을 수 없습니다. : " + id));
        return UserResponseDto.of(user);
    }

    //프론트 router 체크를 위한 로직
    public UserResponseDto getUserInfo(User user) {
        return UserResponseDto.of(user);
    }


    @Transactional
    public void updatePassword (UpdatePasswordRequestDto requestDto, User user) {
        User loginedUser = findUser(user.getUsername());
        //입력한 비밀번호와 DB 내 비밀번호가 동일할 시
        if (passwordEncoder.matches(requestDto.getPassword(), loginedUser.getPassword())) {

            //가장 최근 사용한 3개의 비밀번호를 가져오기
            List<String> usedPasswords = passwordManagerRepository.findPasswordTopThree(loginedUser);
            System.out.println("로그:"+usedPasswords);
            for(String usedPassword : usedPasswords){

                //바꾸려는 비밀번호가 패스워드관리테이블 내 최근 3개의 비밀번호 중 하나와 일치하는 경우
                if(passwordEncoder.matches(requestDto.getUpdatePassword(),usedPassword)){
                    System.out.println("최근 3회 이내 사용된 비밀번호와 같아서 비밀번호를 변경 할 수 없습니다.");
                    throw new IllegalArgumentException("최근 3회 이내 사용된 비밀번호로는 변경 할 수 없습니다.");
                }
                System.out.println("최근 3회 이내 사용된 비밀번호와 달라서 비밀번호를 변경 했습니다.");

            }

            //비밀번호 변경
            String newPassword = passwordEncoder.encode(requestDto.getUpdatePassword());
            loginedUser.updatePassword(newPassword);

            //비밀번호 관리테이블에 추가
            PasswordManager passwordManager = new PasswordManager(newPassword, user);
            passwordManagerRepository.save(passwordManager);
        }
        else
            throw new IllegalArgumentException("입력한 현재 비밀번호가 일치하지 않습니다.");

    }

    @Transactional
    public void updateImage(MultipartFile file, User user) {
        User targetUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        if (!targetUser.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // file 비어있는지 확인
        try {
            if (file != null) {
                String storedFileName = fileComponent.upload(file);

                // 이미 프로필 이미지가 있는 경우 업데이트, 없으면 새로 생성
                Optional<Image> optionalUserImage = imageRepository.findByUserId(user.getId());
                Image userImage;
                if (optionalUserImage.isPresent()) {
                    userImage = optionalUserImage.get();
                    userImage.update(storedFileName);
                } else {
                    userImage = Image.builder().user(user).imageUrl(storedFileName).build();
                }

                // 이미지 중복 확인 (이미 사용 중인 이미지는 아닌지)
                if (imageRepository.existsByImageUrlAndId(storedFileName, user.getId())) {
                    throw new IllegalArgumentException("이미 사용 중인 이미지입니다.");
                }

                // userImage를 저장하거나 업데이트
                imageRepository.save(userImage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public ResponseEntity<ApiResponseDto> checkId(String username) {
        if(userRepository.findByUsername(username).isEmpty()){
            return ResponseEntity.ok().body(new ApiResponseDto("중복체크 성공", HttpStatus.OK.value()));
        }else {
            return ResponseEntity.badRequest().body(new ApiResponseDto("중복된 회원 아이디입니다.", HttpStatus.OK.value()));

        }
    }

    //user가 db내 존재하는지 검사
    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다.")
        );
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<String> withdrawUser(User user) {

        User targetUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        targetUser.denyUser();

        // Withdrawal 엔티티를 생성하고 예정 탈퇴 시간을 설정
        LocalDateTime requestTime = LocalDateTime.now();
        LocalDateTime expireTime = LocalDateTime.now().plusDays(7);

        // 이미 탈퇴 진행 중인지 확인
        Optional<Withdrawal> existingWithdrawal = withdrawalRepository.findByUserId(targetUser.getId());
        if (existingWithdrawal.isPresent()) {
            return ResponseEntity.badRequest().body("이미 탈퇴 진행 중입니다.");
        } else{
            Withdrawal withdrawal = Withdrawal.builder()
                    .user(targetUser) // 사용자 엔티티를 설정
                    .requestTime(requestTime)
                    .expireTime(expireTime)
                    .build();
            withdrawalRepository.save(withdrawal); // Withdrawal 정보 저장

            return ResponseEntity.ok("탈퇴 요청 완료");
        }


    }
}
