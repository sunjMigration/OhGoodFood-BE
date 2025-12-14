package kr.co.ohgoodfood._legacy.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * [DTO / ENUM] PickupStatus
 *
 * - 확장성 및 유지보수성을 위해 ENUM type으로 구성
 * - .displayName을 이용하면 지정한 string을 화면에서 사용할 수 있습니다.
 */

@Getter
@RequiredArgsConstructor
public enum PickupStatus {
    TODAY("오늘픽업"),
    TOMORROW("내일픽업"),
    SOLD_OUT("매진"),
    ERROR("임시 오류"),
    CLOSED("마감");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
