package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.*;

import java.util.List;

import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.ProductDetail;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.UserMypage;

/**
 * UsersService interface
 * - UsersService 기능 틀 interface
 * - 유지 보수 및 확장 편의성을 위해 interface로 구성한다.
 */
public interface UsersService {
    /* 사용자 기본 정보 한 건 조회*/
    UserMypage getUserInfo(String userId);

    /* 리뷰 리스트 여러 건 조회 */
    List<Review> getUserReviews(String userId);

    /* 마이페이지 전체 조립 (유저정보+리뷰리스트) */
    UserMypage getMypage(String userId);

    //[Controller 로직]     
    /* 상품 상세 정보 조회 */
    ProductDetail getProductDetail(int productId);

    /* 상품(가게)별 리뷰 조회 */
    List<Review> getReviewsByProductNo(int productNo);

    /* 상품(가게)별 사진 조회*/
    List<String> getProductImages(int product_no);

    /* 북마크 조회 */
    boolean isBookmarked(String user_id, String store_id);

    /* 예약 처리 메서드 (추후 개발) */
    boolean reserveProduct(String userId, int productId);

    /* 아이디 중복 체크 */
    boolean isDuplicateId(String user_id);

    /* 회원가입 처리 */
    void registerUser(Account account);

    /* 모든 리뷰를 조회 */
    List<Review> getAllReviews(int page, int size);

    /* 리뷰 업데이트 */
    // 화면에 뿌릴 주문·상품·가게 정보 조회
    ReviewForm getReviewForm(int orderNo);

    // 실제 리뷰 저장 (이미지 포함)
    void writeReview(ReviewForm form, String userId);

    /* 가게 이미지 하나 가져오기 */
    String getStoreImg(String store_id);

    /* 포인트 조회 */
    int getUserPoint(String user_id);

}
