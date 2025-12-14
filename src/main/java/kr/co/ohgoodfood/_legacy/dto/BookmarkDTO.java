package kr.co.ohgoodfood._legacy.dto;

import lombok.*;

import java.util.List;

/**
 * [DTO] BookmarkDTO
 *
 * - Bookmark에 나오는 가게 정보 카드를 사용하기 위한 DTO 클래스
 * - 카드 안에 들어가는 모든 정보들을 한 번에 저장해서 사용한다.
 * - @Data로 불필요한 애노테이션을 늘리는 것 보다는, 필요한 애노테이션만 더해주었다.
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor //Builder와 테스트용 생성자
@NoArgsConstructor  // MyBatis 매핑용 기본 생성자
public class BookmarkDTO {
    //Store table에서 가져오는 정보들
    private Store store;
    //Product table에서 가져오는 정보들
    private Product product;
    //Image table에서 가져오는 정보들
    private Image image;
    //Bookmark에서 가져오는 정보들
    private Bookmark bookmark;

    //[추가 정보] DB에는 없는 추가 정보
    private PickupStatus pickup_status; //오늘픽업인지 내일 픽업인지 마감인지를 저장
    private List<String> category_list; //카테고리 리스트 저장
    private List<String> mainmenu_list; //store_menu -> 메인메뉴 리스트 저장
}
