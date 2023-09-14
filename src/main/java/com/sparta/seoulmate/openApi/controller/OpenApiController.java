package com.sparta.seoulmate.openApi.controller;

import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.entity.User;
import com.sparta.seoulmate.openApi.dto.ItemListResponseDto;

import com.sparta.seoulmate.openApi.dto.ItemResponseDto;
import com.sparta.seoulmate.openApi.service.OpenApiService;
import com.sparta.seoulmate.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seoul")
public class OpenApiController {

    private final OpenApiService openApiService;

    // 접수중 서비스 전체 가져오기
    @GetMapping("/services")
    public ResponseEntity<ItemListResponseDto> getAllService() {
        ItemListResponseDto result = openApiService.getAllService();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/services/recommend")
    public ResponseEntity<ItemListResponseDto> getRecommendService(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemListResponseDto result = openApiService.getRecommendService(userDetails.getUser());
        return ResponseEntity.ok().body(result);
    }

    //서비스 데이터 단건 조회
    @GetMapping("/service")
    public ResponseEntity<ItemResponseDto> getService(@RequestParam("svcid") String svcid) {
        ItemResponseDto result = openApiService.getService(svcid);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/service/like")
    public ResponseEntity likeService(@RequestParam("svcid") String svcid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemResponseDto itemResponseDto;

        try {
            itemResponseDto = openApiService.likeService(svcid, userDetails.getUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("처리중 오류 발생 :" + e);
        }
        return ResponseEntity.ok().body(itemResponseDto);
    }

    @DeleteMapping("/service/like")
    public ResponseEntity deleteLikeService(@RequestParam("svcid") String svcid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ItemResponseDto itemResponseDto;

        try {
            itemResponseDto = openApiService.deleteLikeService(svcid, userDetails.getUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("처리중 오류 발생 :" + e);
        }
        return ResponseEntity.ok().body(itemResponseDto);
    }
    @GetMapping("/like/list/{id}")
    public ResponseEntity<ItemListResponseDto> getLikeList(@PathVariable Long id) {
        ItemListResponseDto result = openApiService.getLikeList(id);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDto> updateDatabase() {
        openApiService.getCountAllService();
        return ResponseEntity.status(201).body(new ApiResponseDto("데이터베이스 업데이트 성공", HttpStatus.CREATED.value()));
    }
}
