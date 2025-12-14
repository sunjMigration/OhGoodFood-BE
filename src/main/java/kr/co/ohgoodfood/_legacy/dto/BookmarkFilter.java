package kr.co.ohgoodfood._legacy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [DTO] BookmarkFilter
 *
 * - user가 가진 bookmark를 삭제 & 추가하기 위해 필요한 정보 모음
 * - 유효성 검증을 위해 DTO로 따로 분리하였다.
 * - @Data로 불필요한 애노테이션을 늘리는 것 보다는, 필요한 애노테이션만 더해주었다.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class BookmarkFilter {
    private int bookmark_no;

    private String user_id;

    @NotNull(message = "store_id는 필수값입니다")
    private String store_id; //insert 기능을 위해 추가.
}
