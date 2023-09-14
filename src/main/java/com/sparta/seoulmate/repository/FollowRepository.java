package com.sparta.seoulmate.repository;

import com.sparta.seoulmate.entity.Follow;
import com.sparta.seoulmate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUserAndFollowingUser(User followUser, User followingUser);
    // 팔로우, 팔로잉의 여부 확인
    // 이미 팔로우 했으면 안 찾아지고, 팔로우를 하지 않았으면 찾을 수 있음

    List<Follow> findAllByUserId(Long id);
    // 내가 팔로우 한 사람 정보 찾기
    List<Follow> findAllByFollowingUserId(Long id);
    // 내가 팔로우 한 사람 정보 찾기
}
