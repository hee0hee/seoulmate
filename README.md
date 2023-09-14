# Seoulmate - 서울시 공공서비스 커뮤니케이션 사이트

![seoul mate logo](https://github.com/lazygyu97/seoulmate/assets/105356296/436eafb1-ae8c-40bc-a822-12e681503cec)


- 서울시에서 제공하는 진료,  문화 행사,  교육,  시설 대관,  체육 시설,  공공 서비스,  예약 등의 정보를 사용자들에게 제공한다.
- 참고 : 아래의 공공 API
    
          https://www.data.go.kr/data/15051731/openapi.do
- 서울시 내의 근거리 거주자들 간 행사 동행, 교육 동반 등 커뮤니케이션 공간으로 활용한다.

### [SEOUL mate S.A](https://periwinkle-politician-2a5.notion.site/S-A-5-SEOUL-mate-29412467908b4fffa0dc3b1ac01d5d51?pvs=4)
### [SEOUL mate 발표 자료](https://stripe-meteorite-a08.notion.site/8ce605cb280d44c099c9fa27dc997cb0?pvs=4)
### [SEOUL mate One-Page Notion](https://nonchalant-acrylic-a3f.notion.site/SEOUL-mate-2dfbc02a05a44f8cb8a60a8b4abcbf51?pvs=4)


# 기술 스택
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
## Back-End
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> 


<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> 
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">

## Front-End
<img src="https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white">

# 프로젝트 요구 사항
<details>
<summary> 학습 목표 </summary>
  
#### 1. 공공 API에서 제공받은 데이터 관리하기
    
공공 API 제공하는 다양한 데이터들을 적절히 가져와서 활용하는 경험과 스케줄을 통한 자동 업데이트를 활용한다.
     
    
#### 2. 대규모 데이터 처리 경험하기

공공 API에서 제공하는 데이터베이스를 자체 데이터베이스로 옮겨 대규모 데이터베이스를 구성하고 Jmeter, nGrinder 와 같은 부하 테스트 도구를 활용하여 처리 시간의 개선을 경험해본다. 

#### 3. CI/CD 구축하기

CI/CD를 위한 파이프라인을 구성하고, 안정감 있는 서비스를 배포하여 운영하는 환경을 구성해본다.

#### 4. SSE와 웹소켓을 적용해보기

 실시간으로 사용자에게 데이터를 전달하기 위해 SSE와 웹소켓에 대해 연구해보고, 이를 채팅 기능 및 알림 기능에 구현해본다.

#### 5. Test Code 구현, 적용

</details>



<details>
<summary>필수 기능</summary>

## 회원 관련

- [x]  jwt 토큰 발급을 통한 로그인
- [x]  소셜 로그인 (카카오, 구글, 네이버)
- [x]  일반 로그인
- [x]  관심사 입력 받기(이후 추천 게시물에서 활용)
- [x]  거주지,나이,성별등 추가적인 사용자 정보를 입력 받아 회원가입하기 (이후 추천 게시물에서 활용)
- [x]  이메일 인증 후 회원가입하기 (Redis 활용)
- [x]  전화번호 문 인증 후 회원가입하기 (Redis 활용)
- [x]  비밀번호 변경기능 (3회 이내 변경 내역이 있는 비밀번호는 불가)
- [x]  프로필 수정 (프로필 이미지, 주소, 닉네임)
- [x]  프로필 조회 (조회한 유저의 공공 서비스 스크랩 리스트도 받아오기)
- [x]  레디스 블랙리스트를 통한 로그아웃 기능 구현
- [x]  레디스 리프레시 토근을 통한 인증 인가 구현
- [x]  팔로우 언팔로우 기능 구현
- [x]  팔로우, 팔로인 조회 기능 구현

## 게시글 관련

- [x]  아마존 S3를 통한 게시글에 이미지 담기 (3개 이하)
- [x]  페이징 처리를 통한 게시물 조회
- [x]  게시글 좋아요 및 해제 기능 구현
- [x]  댓글 CRUD 구현
- [x]  댓글 좋아요 및 해제 기능 구현

## 관심사 관련 (게시글 및 공공 서비스 추천을 위한 기능)

- [x]  Enum 을 활용한 카테고리 및 관심사 정의
- [x]  회원별 관심사 등록
- [x]  회원별 관심사 수정
- [x]  회원별 관심사 목록 조회

### 공공 서비스 페이지

- [x]  공공 API를 활용한 서울시 공공 서비스 조회 (카테고리 별 조회, 검색 조회)
- [x]  가져온 공공 서비스 정보들을 데이터베이스에 자체적으로 스케쥴 활용하여 저장 및 업데이트
- [x]  가져온 공공 서비스 정보들을 기반으로 카테고리별 조회 (페이징 처리를 기반)
- [x]  가져온 공공 서비스 정보들을 기반으로 단건 조회
- [x]  관심있는 공공 서비스 좋아요 및 해제 (스크랩 기능)

</details>

<details>
<summary>추가 기능</summary>

## 회원 관련

- [x]  주소 검색 API를 활용한 사용자 주소 입력받기
- [x]  자동 로그인 기능 구현
- [x]  회원 탈퇴 (1개월 보류 기간 설정)

## CI/CD

- [x]  CI 환경 구성 : JUNIT을 통한 깃 풀리퀘스트시 테스트 코드 수행 및 확인
- [ ]  CD 환경 구성 : 학습중…

## 알림

- [x]  다른 회원이 좋아요 및 댓글을 작성 시 알림(작성한 게시글, 댓글)
- [x]  다른 회원이 팔로우 시 알림
- [ ]  채팅이 수신 되면 알림

## 채팅

- [x]  공공 서비스를 이용하기 위한 인원들에 대한 단체 채팅방
- [ ]  지역별 오픈채팅방 구현

## 공공 서비스 및 게시글 추천

- [x]  회원가입 때 입력받은 사용자 정보를 기반으로 지역별, 좋아요 수와 같은 기준으로 사용자에게 공공 서비스 및 게시글을 추천해주는 기능 구현

</details>

# 프로젝트 명세

## [API 명세서](https://www.notion.so/S-A-5-SEOUL-mate-29412467908b4fffa0dc3b1ac01d5d51?pvs=4#824aa7ce20904c05b7bf17f7e4a09152)

## ERD 다이어그램

<hr>

## 팀원

| 리더 | 부리더 | 팀원 | 팀원 |
|------|------|------|------|
| 박현규| 조아영 | 김진희 | 임남명 |
| [GitHub](https://github.com/lazygyu97) | [GitHub](https://github.com/ayboori) | [GitHub](https://github.com/hee0hee) | [GitHub](https://github.com/nm0628) |
| [Velog](https://velog.io/@code_park) | [Velog](https://velog.io/@ayoung3052) | [Velog](https://velog.io/@heeh) | [Velog](https://velog.io/@nmuud) |
