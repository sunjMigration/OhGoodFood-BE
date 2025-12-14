package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dao.UserBookmarkMapper;
import kr.co.ohgoodfood._legacy.dto.BookmarkDTO;
import kr.co.ohgoodfood._legacy.dto.BookmarkFilter;
import kr.co.ohgoodfood._legacy.dto.PickupStatus;
import kr.co.ohgoodfood.global.exception.InvalidPickupDataException;
import kr.co.ohgoodfood.util.StringSplitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserBookmarkServiceImpl.java - UserBookmarkService interface 구현체
 *
 * @see UserBookmarkService - 세부 기능은 해당 클래스인 UserBookmarkServiceImpl에 구현한다.
 * - 의존성 주입은 생성자 주입으로 구성한다.
 * - 스프링은 기본 빈 주입이 싱글톤이기 때문에, 따로 싱글톤 처리 없이 @Service로 해결한다.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserBookmarkServiceImpl implements UserBookmarkService {
    private final UserBookmarkMapper userBookmarkMapper;
    private final UserCommonService userCommonService;

    /**
     * 사용자가 가진 북마크 리스트를 가져오는 method
     *
     * @param user_id           : 현재 세션에 접속한 사용자 id
     * @return                  : bookmarkList (Bookmark DTO의 리스트 객체)
     */
    @Override
    public List<BookmarkDTO> getBookmarkList(String user_id){
        List<BookmarkDTO> bookmarkList = userBookmarkMapper.selectAllBookmark(user_id);

        // 여기에 카테고리 이름과 pickup 상태를 저장
        for(BookmarkDTO bookmark : bookmarkList){
            PickupStatus pickup_status;
            try{
                //product가 없음, 마감 상태 getProduct().으로 접근할때 nullpointerException을 막기 위함이다.
                if(bookmark.getProduct() == null){
                    pickup_status = PickupStatus.CLOSED;
                }else{
                    pickup_status = userCommonService.getPickupDateStatus(
                            bookmark.getStore().getStore_status(),
                            bookmark.getProduct().getPickup_start(),
                            bookmark.getProduct().getAmount()
                    );
                }
            } catch (InvalidPickupDataException e){
                log.info("픽업 상태 계산 실패(storeId={}): {}",
                        bookmark.getStore().getStore_id(), e.getMessage());
                //이상 데이터 값의 경우, continue로 숨김처리 및 pickup_status 계산 안함
                continue;
            }
            bookmark.setPickup_status(pickup_status);
            bookmark.setCategory_list(userCommonService.getCategoryList(bookmark.getStore()));
            bookmark.setMainmenu_list(StringSplitUtils.splitMenu(bookmark.getStore().getStore_menu(), "\\s*\\|\\s*"));
        }

        return bookmarkList;
    }

    /**
     * 북마크를 삭제하기 위한 기능이다.
     *
     * @param bookmarkFilter     : Bookmark 삭제시 필요한 정보값이 담긴 DTO
     * @return                   : 실행 결과 행 수에 따라 Boolean
     */
    @Override
    public boolean deleteUserBookMark(BookmarkFilter bookmarkFilter) {
        String user_id = bookmarkFilter.getUser_id();
        String store_id = bookmarkFilter.getStore_id();

        int cnt = userBookmarkMapper.deleteBookmark(user_id, store_id);

        if (cnt == 1) {
            return true;
        }
        return false; //delete 실패!
    }

    /**
     * 북마크를 추가하기 위한 기능이다.
     *
     * @param bookmarkFilter     : Bookmark 삭제시 필요한 정보값이 담긴 DTO
     * @return                   : 결과 행 수에 따라 Boolean
     */
    @Override
    public boolean insertUserBookMark(BookmarkFilter bookmarkFilter) {
        String user_id = bookmarkFilter.getUser_id();
        String store_id = bookmarkFilter.getStore_id();

        int cnt = userBookmarkMapper.insertBookmark(user_id, store_id);

        if (cnt == 1) {
            return true;
        }
        return false; //insert 실패!
    }
}
