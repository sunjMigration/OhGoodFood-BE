package kr.co.ohgoodfood._legacy.dto;

import lombok.*;

/**
 * [DTO] Bookmark
 *
 * - user가 가진 bookmark DTO
 * - Service로직을 사용하기 위해 MainStore를 상속받도록 구성한다.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class Bookmark{
    private Integer bookmark_no;
    private String user_id;
    private String store_id;
}
