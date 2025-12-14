package kr.co.ohgoodfood._legacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [DTO] UserOrderFilter.java
 *
 * - Order List 정보를 띄울때, filtering을 위한 dto
 * - ajax 요청에서 들어가는 모든 정보를 여기에 넣어서 구성한다.
 */

@Data
@NoArgsConstructor
public class UserOrderFilter {
    //Order 정보를 가져오기 위한 user_id
    private String user_id;

    //Order 상태 정보로 필터링 하기 위함
    private List<String> order_status;
}
