package com.sparta.seoulmate.service;

import com.sparta.seoulmate.dto.user.FollowResponseDto;
import com.sparta.seoulmate.dto.post.PostResponseDto;
import com.sparta.seoulmate.entity.Follow;
import com.sparta.seoulmate.entity.Post;
import com.sparta.seoulmate.entity.User;
import com.sparta.seoulmate.repository.FollowRepository;
import com.sparta.seoulmate.repository.PostRepository;
import com.sparta.seoulmate.repository.UserRepository;
import com.sparta.seoulmate.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    // 내가 팔로우 한 사람 조회
    public List<FollowResponseDto> viewFollowingList(Long id) {
        List<Follow> followList = followRepository.findAllByUserId(id);
        List<FollowResponseDto> followingResponseList = new ArrayList<>();
        for (Follow follow : followList) {
            FollowResponseDto followResponseDto = FollowResponseDto.builder()
                    .id(follow.getFollowingUser().getId())
                    .nickname(follow.getFollowingUser().getNickname())
                    .image(follow.getFollowingUser().getImage() != null ? follow.getFollowingUser().getImage().getImageUrl() : "default")
                    .build();
            followingResponseList.add(followResponseDto);
        }
        return followingResponseList;
    }

    // 나를 팔로우 한 사람 조회
    public List<FollowResponseDto> viewFollowerList(Long id) {
        List<Follow> followList = followRepository.findAllByFollowingUserId(id);
        List<FollowResponseDto> followerResponseList = new ArrayList<>();
        for (Follow follow : followList) {
            FollowResponseDto followResponseDto = FollowResponseDto.builder()
                    .id(follow.getUser().getId())
                    .nickname(follow.getUser().getNickname())
                    .image(follow.getUser().getImage() != null ? follow.getUser().getImage().getImageUrl() : "default")
                    .build();
            followerResponseList.add(followResponseDto);
        }
        return followerResponseList;
    }


    // 내가 팔로우 한 사람의 포스트 조회
    public List<PostResponseDto> viewFollowingPostList(User user) {
        List<PostResponseDto> postList = new ArrayList<>();
        List<Follow> followList = followRepository.findAllByUserId(user.getId()); // 내가 팔로우 한 사람 목록 불러오기
        List<User> userList = followList.stream().map((Follow::getFollowingUser)).toList(); // 내가 팔로우 한 사람 목록을 UserList에 넣어주기
        
        for (User foundUser : userList){
            List<Post> foundPostList =  postRepository.findAllByAuthor(foundUser);
            postList.addAll(foundPostList.stream().map(PostResponseDto::of).toList());
        }
        return postList;
    }

    // 팔로우 하기
    @Transactional
    public String following(@AuthenticationPrincipal UserDetailsImpl userDetails, Long id) {
        // 토큰 체크
        User followerUser = userDetails.getUser();

        if (followerUser == null) {
            throw new IllegalArgumentException("로그인을 해주세요");
        }

        // 팔로우 할 유저 조회
        User followingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        // 본인을 팔로우 할 경우 예외 발생
        if (followerUser.getId().equals(followingUser.getId())) {
            throw new IllegalArgumentException("본인을 팔로우 할 수 없습니다.");
        }

        // 중복 팔로우 예외 발생
        // followRepository 에서 두 개의 Id 값이 존재하는지 확인
        if (followRepository.findByUserAndFollowingUser(followerUser, followingUser).isPresent()) {
            throw new IllegalArgumentException("팔로우가 중복되었습니다.");
        }

        // followRepository DB 저장
        Follow follow = followRepository.save(Follow.builder()
                .user(followerUser)
                .followingUser(followingUser)
                .followingDateTime(LocalDateTime.now())
                .build());

        notificationService.followNotification(follow);

        return followingUser.getNickname();
    }

    // 언팔로우 하기
    @Transactional
    public String unfollowing(@AuthenticationPrincipal UserDetailsImpl userDetails, Long id) {
        // 토큰 체크
        User followerUser = userDetails.getUser();

        if (followerUser == null) {
            throw new IllegalArgumentException("로그인을 해주세요");
        }

        // 언팔로우 할 유저 조회
        User followingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Follow follow = followRepository.findByUserAndFollowingUser(followerUser, followingUser)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 아닙니다"));

        // followRepository DB 삭제(팔로우 해제)
        followRepository.delete(follow);

        return followingUser.getNickname();
    }
}