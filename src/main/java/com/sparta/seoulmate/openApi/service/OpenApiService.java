package com.sparta.seoulmate.openApi.service;

import com.sparta.seoulmate.entity.SeoulApi;
import com.sparta.seoulmate.entity.SeoulApiLike;
import com.sparta.seoulmate.entity.User;
import com.sparta.seoulmate.openApi.dto.ItemListResponseDto;
import com.sparta.seoulmate.openApi.dto.ItemResponseDto;
import com.sparta.seoulmate.openApi.dto.UpdateItemDto;
import com.sparta.seoulmate.openApi.repository.SeoulApiLikeRepository;
import com.sparta.seoulmate.openApi.repository.SeoulApiRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final SeoulApiRepository seoulApiRepository;
    private final SeoulApiLikeRepository seoulApiLikeRepository;
    private final RestTemplate restTemplate;
    private final String API_KEY = "5a65746f7170617238384d474c4e4d/json/";
    private final String SPORT_PATH = "ListPublicReservationSport";
    private final String CULTURE_PATH = "ListPublicReservationCulture";
    private final String MEDICAL_PATH = "ListPublicReservationMedical";
    private final String EDUCATION_PATH = "ListPublicReservationEducation";
    private final String INSTITUTION_PATH = "ListPublicReservationInstitution";

//    private final String DETAIL_PATH = "/json/ListPublicReservationDetail";
//    private final String START_INDEX = "/1";
//    private final String END_INDEX = "/5";


    // 접수중 서비스 전체 가져오기
    public  ItemListResponseDto getAllService() {
        List<SeoulApi> result = seoulApiRepository.findBySVCSTATNM("접수중");
        return ItemListResponseDto.of(result);
    }
  
   // 서비스 추천 로직
    public ItemListResponseDto getRecommendService(User user) {
        List<String> interest = Optional.ofNullable(user.getUserInterests())
                .map(interests -> interests.stream()
                        .map(userInterest -> userInterest.getInterests().getTitle())
                        .collect(Collectors.toList())) // 관심사가 있으면 제목 리스트로 매핑, 없으면 null
                .orElse(Collections.emptyList());

        String address = user.getAddress();
        String serviceStatus = "접수중"; // 서비스 상태

        List<SeoulApi> resultList = new ArrayList<>();

        if (!interest.isEmpty()) {
            for (String randomInterest : interest) {
                List<SeoulApi> apiList = seoulApiRepository.findByAREANMAndMINCLASSNMAndSVCSTATNM(address, randomInterest, serviceStatus)
                        .orElseGet(() -> seoulApiRepository.findByAREANMAndSVCSTATNM(address, serviceStatus)
                                .orElse(Collections.emptyList()));

                resultList.addAll(apiList);

                if (resultList.size() >= 10) {
                    break; // 최대 5개까지만 선택
                }
            }

            if(resultList.size()<10){
                List<SeoulApi> extraList= seoulApiRepository.findByAREANMAndSVCSTATNM(address, serviceStatus)
                        .orElse(Collections.emptyList());
                int extraSize= 10-resultList.size();

                resultList.addAll(extraList.subList(0,extraSize));

            }

        } else {
            resultList = seoulApiRepository.findByAREANMAndSVCSTATNM(address, serviceStatus)
                    .orElse(Collections.emptyList());
        }

        int maxResultCount = Math.min(resultList.size(), 10);
        resultList = resultList.subList(0, maxResultCount);

        return ItemListResponseDto.of(resultList);
    }


    //서비스 데이터 단건 조회
    public ItemResponseDto getService(String svcid) {

        return ItemResponseDto.of(findService(svcid));
    }
    @Transactional
    public ItemResponseDto likeService(String svcid, User user) {
        SeoulApi service = findService(svcid);

        if (seoulApiLikeRepository.existsByUserAndSeoulApi(user, service)) {
            throw new DuplicateRequestException("이미 좋아요 한 서비스 입니다.");
        } else {
            SeoulApiLike seoulApiLike = SeoulApiLike.builder()
                    .seoulApi(service)
                    .user(user)
                    .build();
            seoulApiLikeRepository.save(seoulApiLike);
        }

        return ItemResponseDto.of(service);
    }

    @Transactional
    public ItemResponseDto deleteLikeService(String svcid, User user) {
        SeoulApi service = findService(svcid);
        Optional<SeoulApiLike> seoulApiLike = seoulApiLikeRepository.findByUserAndSeoulApi(user, service);

        if (seoulApiLike.isPresent()) {
            seoulApiLikeRepository.delete(seoulApiLike.get());
        } else {
            System.out.println("해당 게시글에 취소할 좋아요가 없습니다.");
        }
        return ItemResponseDto.of(service);
    }

    //각 카테고리의 데이터 개수 가져오기 (updateDatabase()에서 전체 데이터 베이스를 업데이트 할때 사용.)
    public void getCountAllService() {
        List<String> serviceList = Arrays.asList(SPORT_PATH, INSTITUTION_PATH, CULTURE_PATH, EDUCATION_PATH, MEDICAL_PATH);
        HashMap<String, Integer> countMap = new HashMap<>();
        try {
            for (String service : serviceList) {

                URI uri = UriComponentsBuilder
                        .fromUriString("http://openapi.seoul.go.kr:8088/" + API_KEY + service + "/1/1")
                        .encode(StandardCharsets.UTF_8)
                        .build()
                        .toUri();
                log.info("uri = " + uri);
                ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
                log.info("OPEN API Status Code : " + responseEntity.getStatusCode());

                JSONObject jsonObject = new JSONObject(responseEntity.getBody()).getJSONObject(service);

                int listTotalCount = jsonObject.getInt("list_total_count");

                System.out.println(listTotalCount);

                countMap.put(service, listTotalCount);
            }
            updateAllDatabase(countMap);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("데이터베이스 업데이트에 실패했습니다.: total_count 를 불러오는 중에 문제가 발생했습니다.");

        }

    }

    // 각 카테고리 별 전체조회후 데이터베이스 업데이트
    @Transactional
    public void updateAllDatabase(HashMap<String, Integer> updateMap) {
        try {
            for (Map.Entry<String, Integer> entry : updateMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                log.info(key + " update processing...");

                URI uri = UriComponentsBuilder
                        .fromUriString("http://openapi.seoul.go.kr:8088/" + API_KEY + key)
                        .path("/1/" + value.toString())
                        .encode(StandardCharsets.UTF_8)
                        .build()
                        .toUri();
                log.info("uri = " + uri);

                ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
                log.info("OPEN API Status Code : " + responseEntity.getStatusCode());

                JSONObject jsonObject = new JSONObject(responseEntity.getBody()).getJSONObject(key);
                JSONArray itemsArray = jsonObject.getJSONArray("row");
                List<SeoulApi> itemsToSave = new ArrayList<>();

                for (Object item : itemsArray) {

                    JSONObject jsonObjectItem = (JSONObject) item;
                    String svcid = jsonObjectItem.getString("SVCID");

                    Optional<SeoulApi> updateData = seoulApiRepository.findBySVCID(svcid);

                    if (updateData.isPresent()) {
                        //새로 받아온 데이터가 mysql 데이터베이스 있는 경우.
                        //Transactional을 통한 Entity 업데이트
                        updateData.get().update((JSONObject) item);
                    } else {
                        //새로 받아온 데이터가 mysql에 없는 경우
                        //Repository를 통해 save 해준다.
                        itemsToSave.add(new UpdateItemDto().toEntity(jsonObjectItem));
                    }
                }
                seoulApiRepository.saveAll(itemsToSave);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("데이터베이스 업데이트에 실패했습니다. ");
        }

    }

    private SeoulApi findService(String svcid) {
        return seoulApiRepository.findBySVCID(svcid).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 서비스 입니다.")
        );
    }

    public ItemListResponseDto getLikeList(Long id) {
        List<SeoulApiLike> resultList= seoulApiLikeRepository.findByUserId(id);
        return ItemListResponseDto.of2(resultList);
    }
}
