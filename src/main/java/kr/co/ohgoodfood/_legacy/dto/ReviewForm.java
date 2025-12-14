package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *  ReviewWrite용 DTO
 */
@Data
@NoArgsConstructor
public class ReviewForm {
	// Account table에서 가져오는 정보들
	private String user_id;
	private String user_nickname;
	private int user_point;
	
    // Product table에서 가져오는 정보들
    private int    sale_price;
    
    // Orders table에서 가져오는 정보들
    private int order_no;
    private int quantity;
    private int product_no;
    
    // Store table에서 가져오는 정보들
    private String store_name;
    
    // Review table에 업데이트 되는 정보들
    private int review_no;
    private String review_content;
    private Timestamp writed_at;
    private String is_blocked;
    private String review_img;
    private String store_id;
    private String store_img;

    
    // 업로드용
    private MultipartFile imageFile;   // <input name="imageFile">
    private int total_price;
    
}
