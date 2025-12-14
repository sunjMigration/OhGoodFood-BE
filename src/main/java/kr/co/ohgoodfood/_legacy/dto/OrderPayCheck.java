package kr.co.ohgoodfood._legacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderPayCheck.java
 *
 * - 결제 API 사용 전에 결제 가능한 상태인지 체크할 때 사용하기 위한 DTO
 * - store_status로 현재 마감 상태인지를 체크하고
 * - amount로 수량이 충분한지를 체크한다.
 */

@Data
@NoArgsConstructor
public class OrderPayCheck {
    private String store_id;
    private String store_status;
    private int product_no;
    private int amount;
}
