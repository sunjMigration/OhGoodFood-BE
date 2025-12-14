package kr.co.ohgoodfood._legacy.dto;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
 *  productdetail용 DTO
 */
@ToString
@Data
@NoArgsConstructor
public class ProductDetail{
	// Account table에서 가져오는 정보들
    private String user_id;
    private String user_nickname;

    // Store table에서 가져오는 정보들
    private String store_id;
    private String confirmed;
    private String store_address;
    private String store_name;
    private String store_menu;
    private String store_explain;
    private String store_telnumber;
    private String store_status;
    private Time opened_at;
    private Time closed_at;
    private String category_bakery;
    private String category_fruit;
    private String category_salad;
    private String category_others;

    // Product table에서 가져오는 정보들
    private int    product_no;
    private String product_explain;
    private int    origin_price;
    private int    sale_price;
    private int    amount;
    private Timestamp pickup_start;
    private Timestamp pickup_end;
    private Timestamp reservation_end;
    private String status;
    private PickupStatus pickupStatus;
    
    // Review table에서 가져오는 정보들
    private List<Review> reviews;
    private int          reviewCount; // 리뷰 계산에 필요

    // Image table에서 가져오는 정보들
    private String store_img; // 가게 이미지 전체
    private List<String> images;
    
    
    // Bookmark table에서 가져오는 정보들
    private boolean bookmarked;
    private Integer bookmark_no;

    


    
    

}
