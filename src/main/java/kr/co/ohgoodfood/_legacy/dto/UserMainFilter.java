package kr.co.ohgoodfood._legacy.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * [DTO] UserMainStoreFilter.java
 *
 * - Main에서 가게 정보를 사용할때, filtering을 위한 dto
 * - ajax 요청에서 들어가는 모든 정보를 여기에 넣어서 구성한다.
 * - @Data로 불필요한 애노테이션을 늘리는 것 보다는, 필요한 애노테이션만 더해주었다.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class UserMainFilter {
    //지도에서 사용하기 위한 store_id
    private String store_id;

    //카테고리 modal 토글
    @NotNull(message = "category_bakery는 필수값입니다")
    private YesNo category_bakery;

    @NotNull(message = "category_fruit는 필수값입니다")
    private YesNo category_fruit;

    @NotNull(message = "category_salad는 필수값입니다")
    private YesNo category_salad;

    @NotNull(message = "category_others는 필수값입니다")
    private YesNo category_others;

    //예약 가능만 토글
    @NotNull(message = "store_status는 필수값입니다")
    private YesNo store_status;

    @PositiveOrZero(message = "amount는 0 이상이어야 합니다")
    private int amount;

    //오늘예약 & 내일 예약 토글
    @FutureOrPresent(message = "pickupStart는 오늘 또는 이후 날짜여야 합니다")
    private LocalDate pickup_start;

    //[추가] 검색어
    private String search;

    //위도, 경도
    @DecimalMin(value = "-90.0", message = "latitude 범위는 -90 ~ 90 입니다")
    @DecimalMax(value = "90.0",  message = "latitude 범위는 -90 ~ 90 입니다")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "longitude 범위는 -180 ~ 180 입니다")
    @DecimalMax(value = "180.0",  message = "longitude 범위는 -180 ~ 180 입니다")
    private Double longitude;
}
