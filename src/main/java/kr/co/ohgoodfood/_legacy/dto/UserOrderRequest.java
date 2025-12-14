package kr.co.ohgoodfood._legacy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

/**
 * [DTO] UserOrderRequest.java
 *
 * - OrderList에서 필요한 CRUD 기능을 수행하기 위한 DTO
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class UserOrderRequest {
    private String user_id;

    //해당 order의 주문 정보를 update 하는 것이므로, order_no는 not null
    @NotNull(message = "order_no는 필수값입니다")
    private int order_no;

    //해당 poduct의 주문 정보를 update 하는 것이므로, product_no는 not null
    @NotNull(message = "product_no는 필수값입니다")
    private int product_no;

    // 되살리는 주문 수량은 0 이상이어야 한다.
    @PositiveOrZero(message = "quantity는 0 이상이어야 합니다")
    private int quantity;

    //user or store
    private String canceld_from;

    // 주문 상태 (confirmed, pickup, cancel, ready)
    private String order_status;

    private int order_code;
}
