//package kr.co.ohgoodfood._legacy.dao;
//
//import kr.co.ohgoodfood._legacy.dto.BookmarkDTO;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
///**
// * UserBookmarkMapper
// *
// * 사용자 bookmark 페이지에서 사용하는 mapper interface
// */
//@Mapper
//public interface UserBookmarkMapper {
//    /**
//     * 사용자 북마크 화면에서 표시할 가게 목록을 조회
//     *
//     * @param user_id          조회 대상 user_id
//     * @return                 특정 user가 가진 BookmarkDTO 리스트
//     */
//    List<BookmarkDTO> selectAllBookmark(String user_id);
//
//    /**
//     * 사용자의 특정 북마크를 삭제 처리
//     *
//     * @param user_id          조회 대상 user_id
//     * @param store_id         user_id + store_id 조합으로 삭제
//     * @return                 영향받은 행(row) 수
//     */
//    int deleteBookmark(@Param("user_id") String user_id,
//                       @Param("store_id") String store_id);
//
//    /**
//     * 사용자 특정 북마크를 추가 처리
//     *
//     * @param user_id          조회 대상 user_id
//     * @param store_id         북마크에 추가할 store 정보
//     * @return                 영향받은 행(row) 수
//     */
//    int insertBookmark(@Param("user_id") String user_id,
//                       @Param("store_id") String store_id);
//}
