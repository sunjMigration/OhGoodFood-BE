package kr.co.ohgoodfood._legacy.dto;

import lombok.*;

/**
 * [DTO] UserOrderDTO.java
 *
 * - Order List 정보에 필요한 DTO
 * - 카드 안에 들어가는 모든 정보들을 한 번에 저장해서 사용한다.
 * - @Data로 불필요한 애노테이션을 늘리는 것 보다는, 필요한 애노테이션만 더해주었다.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class UserOrderDTO {
    //Store table
    private Store store;

    //Paid table
    private Paid paid;

    //Product table
    private Product product;

    //Order table
    private Orders order;

    //Image table
    private Image image;

    //[추가 정보] DB에는 없는 추가 정보
    private PickupStatus pickup_status; //오늘픽업인지 내일 픽업인지를 저장
    private Boolean block_cancel; //확정 시간 한 시간 전에 취소하지 못하도록 변수 생성
    private Boolean has_review; //리뷰가 존재하는 주문인지 판단하기 위함이다.
    private int point; //주문에 해당하는 point 지급을 위한 컬럼 (order 금액의 1%)
}
