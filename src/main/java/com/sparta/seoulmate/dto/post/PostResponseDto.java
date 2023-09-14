package com.sparta.seoulmate.dto.post;

import com.sparta.seoulmate.dto.ApiResponseDto;
import com.sparta.seoulmate.dto.comment.CommentResponseDto;
import com.sparta.seoulmate.entity.Post;
import com.sparta.seoulmate.entity.Image;
import com.sparta.seoulmate.entity.PostLike;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class PostResponseDto extends ApiResponseDto {
    private Long id;
    private String title;
    private String content;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;
    private List<PostImageResponseDto> images;
    private String author;
    private Long authorId;
    private String authorImage;
    private List<PostLikeResponseDto> postLikes;
    public static PostResponseDto of(Post post) {

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .address(post.getAddress())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .author(post.getAuthor().getNickname())
                .authorId(post.getAuthor().getId())
                .authorImage(
                        Optional.ofNullable(post.getAuthor().getImage())
                                .map(Image::getImageUrl)
                                .orElse("default") // 이미지가 null인 경우에 대한 처리
                )                .comments(post.getComments().stream().map(CommentResponseDto::of).toList())
                .images(post.getImages().stream().map(PostImageResponseDto::of).toList())
                .postLikes(post.getPostLikes().stream().map(PostLikeResponseDto::of).toList())
                .build();
    }
}
