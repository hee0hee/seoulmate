package com.sparta.seoulmate.openApi.repository;

import com.sparta.seoulmate.entity.SeoulApi;
import com.sparta.seoulmate.entity.SeoulApiLike;
import com.sparta.seoulmate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeoulApiLikeRepository extends JpaRepository<SeoulApiLike,Long> {


    boolean existsByUserAndSeoulApi(User user, SeoulApi service);

    Optional<SeoulApiLike> findByUserAndSeoulApi(User user, SeoulApi service);

    List<SeoulApiLike> findByUserId(Long id);
}
