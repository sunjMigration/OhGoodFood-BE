package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.BookmarkDTO;
import kr.co.ohgoodfood._legacy.dto.BookmarkFilter;

import java.util.List;

/**
 * UserBookmarkService interface
 * - UserBookmarkService 기능 틀 interface
 * - 유지 보수 및 확장 편의성을 위해 interface로 구성한다.
 */

public interface UserBookmarkService {
    //[Controller 로직] 북마크 Controller 연결 로직
    List<BookmarkDTO> getBookmarkList(String user_id);

    //[Controller 로직] 북마크 삭제 Controller 연결 로직
    boolean deleteUserBookMark(BookmarkFilter bookmarkFilter);

    //[Controller 로직] 북마크 추가 Controller 연결 로직
    boolean insertUserBookMark(BookmarkFilter bookmarkFilter);
}
