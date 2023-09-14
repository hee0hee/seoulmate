package com.sparta.seoulmate.controller;

import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.dto.chat.ChatRoomListResponseDto;
import com.sparta.seoulmate.dto.chat.ChatRoomResponseDto;
import com.sparta.seoulmate.dto.chat.RoomDto;
import com.sparta.seoulmate.security.UserDetailsImpl;
import com.sparta.seoulmate.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    // SocketTset ws://localhost:8080/ws/chat?uuid={uuid}

    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/chats")
    public ResponseEntity<RoomDto> createChatRoom(@RequestParam(name = "room-name") String roomName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RoomDto chatRoomDto = chatService.createChatRoom(roomName, userDetails.getUser());
        return ResponseEntity.ok().body(chatRoomDto);
    }

    // 채팅방 단건 조회
    @GetMapping("/chat")
    public ResponseEntity<ChatRoomResponseDto> getChatRoom (@RequestParam String uuid) {
        ChatRoomResponseDto chatRoomResponseDto = chatService.getChatRoom(uuid);
        return ResponseEntity.ok().body(chatRoomResponseDto);
    }

    // 채팅방 다건 조회
    @GetMapping("/chats")
    public ResponseEntity<ChatRoomListResponseDto> getChatRooms() {
        ChatRoomListResponseDto chatRoomListResponseDto = chatService.getChatRooms();
        return ResponseEntity.ok().body(chatRoomListResponseDto);
    }


    // 채팅방 삭제
    @DeleteMapping("/chat")
    public ResponseEntity<ApiResponseDto> deleteChatRoom(@RequestParam String uuid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatService.deleteChatRoom(uuid, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("채팅방 삭제 성공", HttpStatus.OK.value()));
    }
}
