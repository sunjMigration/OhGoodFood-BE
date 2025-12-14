package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.PickupStatus;
import kr.co.ohgoodfood._legacy.dto.Store;

import java.sql.Timestamp;
import java.util.List;

/**
 * UserCommonService interface
 * - UserCommonService 기능 틀 interface
 * - 유지 보수 편의성 및, DIP 원칙을 준수하기 위해 interface로 구성한다.
 * - bookmark와 main에서 동시에 사용하는 판별 로직을 common으로 분리
 */

public interface UserCommonService {
    //[판별 로직] 오늘 픽업, 내일 픽업, 마감 판별 연결 로직
    public PickupStatus getPickupDateStatus(String store_status, Timestamp pickup_start, Integer amount);

    //[판별 로직] 카테고리 List<String> 저장 로직
    public List<String> getCategoryList(Store store);
}
