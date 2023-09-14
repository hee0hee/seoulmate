package com.sparta.seoulmate.controller;

import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.dto.user.FollowResponseDto;
import com.sparta.seoulmate.dto.post.PostResponseDto;
import com.sparta.seoulmate.security.UserDetailsImpl;
import com.sparta.seoulmate.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;
  
    @GetMapping
    public String follow(){
        return "follow";
    } // follow.html을 반환
    

    // 내가 팔로우 한 사람 조회
    @GetMapping("/followingList")
    @ResponseBody
    public ResponseEntity<List<FollowResponseDto>> viewFollowingList(@AuthenticationPrincipal UserDetailsImpl userDetail){
        List<FollowResponseDto> followRepositories = followService.viewFollowingList(userDetail.getUser().getId());
        return ResponseEntity.ok().body(followRepositories);
    }

    // 나를 팔로우 한 사람 조회
    @GetMapping("/followerList")
    @ResponseBody
    public ResponseEntity<List<FollowResponseDto>> viewFollowerList(@AuthenticationPrincipal UserDetailsImpl userDetail){
        List<FollowResponseDto> followRepositories = followService.viewFollowerList(userDetail.getUser().getId());
        return ResponseEntity.ok().body(followRepositories);
    }
    //유저가 팔로우 한 사람 조회
    @GetMapping("/{id}/followingList")
    @ResponseBody
    public ResponseEntity<List<FollowResponseDto>> findFollowingList(@PathVariable Long id){
        List<FollowResponseDto> followRepositories = followService.viewFollowingList(id);
        return ResponseEntity.ok().body(followRepositories);
    }

    // 유저를 팔로우 한 사람 조회
    @GetMapping("/{id}/followerList")
    @ResponseBody
    public ResponseEntity<List<FollowResponseDto>> findFollowerList(@PathVariable Long id){
        List<FollowResponseDto> followRepositories = followService.viewFollowerList(id);
        return ResponseEntity.ok().body(followRepositories);
    }

    // 내가 팔로우 한 사람의 포스트 조회
    @GetMapping("/postList")
    @ResponseBody
    public ResponseEntity<List<PostResponseDto>> viewFollowingPostList(@AuthenticationPrincipal UserDetailsImpl userDetail){
        List<PostResponseDto> postResponseDto = followService.viewFollowingPostList(userDetail.getUser());
        return ResponseEntity.ok().body(postResponseDto);
    }

    // 팔로우 하기
    @PostMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> following(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        String nickname = followService.following(userDetails, userId);
        return ResponseEntity.ok().body(new ApiResponseDto(nickname + " 님을 팔로우 했습니다", HttpStatus.OK.value()));
    }

    // 언팔로우 하기
    @DeleteMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> unfollowing(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        String nickname = followService.unfollowing(userDetails, userId);
        return ResponseEntity.ok().body(new ApiResponseDto( nickname + " 님을 팔로우 해제 했습니다", HttpStatus.OK.value()));
    }

}
