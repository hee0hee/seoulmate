package com.sparta.seoulmate.openApi.dto;

import com.sparta.seoulmate.entity.SeoulApi;
import com.sparta.seoulmate.entity.SeoulApiLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemListResponseDto {

    private List<ItemResponseDto> itemList;

    public static ItemListResponseDto of(List<SeoulApi> lists) {

        List<ItemResponseDto> itemResponseDtos = lists.stream().map(ItemResponseDto::of).toList();

        return ItemListResponseDto.builder()
                .itemList(itemResponseDtos)
                .build();

    }
    public static ItemListResponseDto of2(List<SeoulApiLike> lists) {

        List<ItemResponseDto> itemResponseDtos = lists.stream().map(ItemResponseDto::of2).toList();

        return ItemListResponseDto.builder()
                .itemList(itemResponseDtos)
                .build();

    }


}
