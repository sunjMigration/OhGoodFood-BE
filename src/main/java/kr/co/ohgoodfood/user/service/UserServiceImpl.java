package kr.co.ohgoodfood.user.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import kr.co.ohgoodfood.global.config.AwsS3Config;
import kr.co.ohgoodfood._legacy.dao.UserMapper;
import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.ProductDetail;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.ReviewForm;
import kr.co.ohgoodfood._legacy.dto.UserMypage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UsersServiceImpl.java - UsersService interface 구현체
 *
 * @see UsersService - 세부 기능은 해당 클래스인 UsersServiceImpl에 구현한다.
 * 의존성 주입은 생성자 주입으로 구성한다.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UsersService{
    private final UserMapper userMapper;
    private final AwsS3Config awsS3Config;

    /** 유저 정보 한 건 조회 */
    @Override
    public UserMypage getUserInfo(String userId) {
        UserMypage info = userMapper.selectUserInfo(userId);
        return (info != null ? info : new UserMypage());
    }

    /** 리뷰 리스트 여러 건 조회 */
    @Override
    public List<Review> getUserReviews(String userId) {
        return userMapper.selectUserReviews(userId);
    }

    /** 마이페이지 전체 조립 (유저정보+리뷰리스트) */
    @Override
    public UserMypage getMypage(String userId) {
        UserMypage page = getUserInfo(userId);
        page.setReviews(getUserReviews(userId));
        return page;
    }

    /** 제품 상세 보기 */
    @Override
    @Transactional(readOnly = true)
    public ProductDetail getProductDetail(int product_no) {
        // 기본 상품·매장·계정 정보
        ProductDetail detail = userMapper.selectProductInfo(product_no);
        // 이미지 리스트
        detail.setImages(userMapper.selectProductImages(product_no));
        // 리뷰 리스트
        detail.setReviews(userMapper.selectProductReviews(product_no));
        detail.setReviewCount(detail.getReviews().size());
        return detail;
    }

    @Override
    public boolean isBookmarked(String user_id, String store_id) {
        return userMapper.isBookmarked(user_id, store_id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProductImages(int product_no) {
        return userMapper.selectProductImages(product_no);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductNo(int productNo) {
        return userMapper.selectProductReviews(productNo);
    }


    @Override
    @Transactional
    public boolean reserveProduct(String userId, int product_no) {
        // 간단 insert 결과로 성공 여부 판단
        return userMapper.insertReservation(userId, product_no) > 0;
    }

    /** 사용자 회원가입 */
    /** 아이디 중복 체크 */
    @Override
    public boolean isDuplicateId(String user_id) {
        return userMapper.countByUserId(user_id) > 0;
    }

    @Override
    public void registerUser(Account account) {
        // 비밀번호 MD5 해시
        String rawPwd = account.getUser_pwd();
        if (rawPwd != null && !rawPwd.isEmpty()) {
            account.setUser_pwd(md5(rawPwd));
        }

        // 가입일, 상태 기본값 세팅
        account.setJoin_date(new Timestamp(System.currentTimeMillis()));
        account.setUser_status("ACTIVE");

        // *디버그: 최종 저장될 Account 객체 내용 확인
        System.out.println("최종 저장 정보: " + account);

        // DB 저장 (한 번만)
        int cnt = userMapper.insertUser(account);
        System.out.println("insertUser 반환값: " + cnt);
        if (cnt != 1) {
            throw new RuntimeException("회원가입 실패 (insertUser 반환값=" + cnt + ")");
        }
    }

    /** MD5 해시 유틸 */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 암호화 오류", e);
        }
    }

    /**
     * 메뉴바 review 탭
     * */

    @Override
    public List<Review> getAllReviews(int page, int size) {
        int startIdx = (page - 1) * size;
        return userMapper.getAllReviews(startIdx, size);
    }


    /**
     * 리뷰 이미지 AWS S3에 업로드하고, public URL을 반환
     */
    // GET: orderNo로 DTO 채우기
    @Override
    @Transactional(readOnly = true)
    public ReviewForm getReviewForm(int orderNo) {
        return userMapper.selectReviewFormByOrderNo(orderNo);
    }

    // POST: 이미지 업로드 후 DB INSERT
    @Override
    @Transactional
    public void writeReview(ReviewForm form, String userId) {
        form.setUser_id(userId);

        ReviewForm info = userMapper.selectReviewFormByOrderNo(form.getOrder_no());
        form.setTotal_price(info.getTotal_price());

        // — 이미지 업로드 —
        MultipartFile imgFile = form.getImageFile();
        if (imgFile != null && !imgFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imgFile.getOriginalFilename();
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(imgFile.getContentType());
            meta.setContentLength(imgFile.getSize());

            // InputStream은 try‐with‐resources 로 안전하게 열고 닫기
            try (InputStream is = imgFile.getInputStream()) {
                awsS3Config.amazonS3()
                        .putObject(new PutObjectRequest(
                                awsS3Config.getBucket(),
                                fileName,
                                is,
                                meta
                        ));
            } catch (IOException e) {
                throw new UncheckedIOException("리뷰 이미지 업로드 실패", e);
            }

            form.setReview_img(fileName);
        }


        // 리뷰 저장
        userMapper.insertReview(form);

        // 포인트 적립
        userMapper.addUserPoint(form);
    }
    // AWS S3 인스턴스 반환
    private AmazonS3 amazonS3() {
        return awsS3Config.amazonS3();
    }

    /* 가게 이미지 하나 가져오기 */
    @Override
    public String getStoreImg(String store_id) {
        return userMapper.selectStoreImg(store_id);
    }

    /* 포인트 조회 */
    @Override
    public int getUserPoint(String user_id) {
        return (Integer)userMapper.selectUserPoint(user_id) == null ? 0 : userMapper.selectUserPoint(user_id);
    }

}