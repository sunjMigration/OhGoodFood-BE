package kr.co.ohgoodfood._legacy.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * [DTO / ENUM] StoreCategory
 *
 * - 확장성 및 유지보수성을 위해 ENUM type으로 구성
 * - .displayName을 이용하면 지정한 string을 화면에서 사용할 수 있습니다.
 */

@Getter //display name을 쉽게 가져오기 위함
@RequiredArgsConstructor //생성자주, 주어진 한글 인자로 인스턴스 생성
public enum StoreCategory {
    BAKERY("빵 & 디저트"),
    FRUIT("과일"),
    SALAD("샐러드"),
    ETC("그 외");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
