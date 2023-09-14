package com.sparta.seoulmate.controller;

import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.dto.post.PostListResponseDto;
import com.sparta.seoulmate.dto.post.PostRequestDto;
import com.sparta.seoulmate.dto.post.PostResponseDto;
import com.sparta.seoulmate.security.UserDetailsImpl;
import com.sparta.seoulmate.service.PostService;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<ApiResponseDto> createPost(@RequestPart(value = "title") String title, @RequestPart(value = "content") String content, @Nullable @RequestPart(value = "file") List<MultipartFile> files, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostRequestDto requestDto = PostRequestDto.builder().title(title).content(content).build();
        postService.createPost(requestDto, files, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("게시글 생성 성공", HttpStatus.OK.value()));
    }

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sortBy") String sortBy, @RequestParam("isAsc") boolean isAsc, @RequestParam(value = "address", required = false) String address) {
        Page<PostResponseDto> result;
        if (address != null && !address.isEmpty()) {
            // 주소(address)를 이용한 페이징 처리
            result = postService.getPostsByAddress(page - 1, size, sortBy, isAsc, address);
        } else {
            // 주소가 주어지지 않은 경우 전체 게시물에 대한 페이징 처리
            result = postService.getPosts(page - 1, size, sortBy, isAsc);
        }
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/posts/all")
    public ResponseEntity<PostListResponseDto> getAllPosts() {
        PostListResponseDto result = postService.getAllPosts();
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/posts/list/{id}")
    public ResponseEntity<PostListResponseDto> getUserPost(@PathVariable Long id) {
        PostListResponseDto result = postService.getUserPost(id);
        return ResponseEntity.ok().body(result);
    }

    // 게시글 검색(title, content, title + content)
    @GetMapping("/posts/search")
    public ResponseEntity<Page<PostResponseDto>> searchPosts(@NotNull @RequestParam("keyword") String keyword, // 검색 키워드 파라미터
                                                             @RequestParam("criteria") String criteria, // 검색 조건 파라미터 (title, content, title + content 중 하나)
                                                             @RequestParam("page") int page, @RequestParam("size") int size) {
        if (keyword.length() < 2) {
            // 검색어 길이가 2자 미만일 경우 에러 응답을 반환하거나, 다른 처리를 할 수 있다
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page - 1, size); // 페이지와 항목 수를 기반으로 페이징 정보 생성
        Page<PostResponseDto> searchResultPage = postService.searchPosts(keyword, criteria, pageable);
        return ResponseEntity.ok().body(searchResultPage);
    }

    // 게시글 단건 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        PostResponseDto result = postService.getPostById(id);

        return ResponseEntity.ok().body(result);
    }

    // 게시글 업데이트
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> updatePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        try {
            PostResponseDto result = postService.updatePost(id, requestDto, userDetails.getUser());
            return ResponseEntity.ok().body(result);
        } catch (RejectedExecutionException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("작성자만 수정 할 수 있습니다.", HttpStatus.BAD_REQUEST.value()));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> deletePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        try {
            postService.deletePost(id, userDetails.getUser());
            return ResponseEntity.ok().body(new ApiResponseDto("게시글 삭제 성공", HttpStatus.OK.value()));
        } catch (RejectedExecutionException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("작성자만 삭제 할 수 있습니다.", HttpStatus.BAD_REQUEST.value()));
        }
    }

    // 게시글 좋아요
    @PostMapping("/posts/{id}/likes")
    public ResponseEntity likePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        PostResponseDto postResponseDto;

        try {
            postResponseDto = postService.likePost(id, userDetails.getUser());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 작성중 오류가 발생했습니다.");
        }


        return ResponseEntity.ok().body(postResponseDto);
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/posts/{id}/likes")
    public ResponseEntity deleteLikePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        PostResponseDto postResponseDto;

        try {
            postResponseDto = postService.deleteLikePost(id, userDetails.getUser());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 작성중 오류가 발생했습니다.");
        }
        return ResponseEntity.ok().body(postResponseDto);
    }
}
