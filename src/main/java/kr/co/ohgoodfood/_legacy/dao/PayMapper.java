//package kr.co.ohgoodfood._legacy.dao;
//
//import java.util.Map;
//
//import org.apache.ibatis.annotations.Mapper;
//
//import kr.co.ohgoodfood._legacy.dto.Orders;
//import kr.co.ohgoodfood._legacy.dto.Paid;
//
//@Mapper
//public interface PayMapper {
//
//    // 주문 정보 삽입
//    public void insertOrder(Orders orders);
//    // 결제 정보 삽입
//    public void insertPaid(Paid paid);
//    // 상품 재고 가져오기
//    public int getProductAmount(int product_no);
//    // Piad_code로 Paid 상태 업데이트
//    public void updatePaidStatusByPaidCode(String paid_code);
//    // paid_code로 주문 상태 업데이트
//    public void updateOrderStatusByPaidCode(String paid_code);
//    // paid_code로 상품 수량 업데이트
//    public void updateProductAmount(Map<String, Object> map);
//    // paid_code로 주문 수량 가져오기
//    public int getOrderQuantityByPaidCode(String paid_code);
//    // paid_code로 상품 재고 가져오기
//    public int getProductAmountByPaidCode(String paid_code);
//    // paid_code로 주문 canceld_from 업데이트
//    public void updateOrderCanceldFromByPaidCode(String paid_code);
//    // paid_code로 주문 canceld_from 가져오기
//    public String getOrderCanceldFromByPaidCode(String paid_code);
//    // 가게 상태 확인하기
//    public String getStoreStatus(String store_id);
//    // Paid_code로 가게 상태 확인하기
//    public String getStoreStatusByPaidCode(String paid_code);
//    // Paid_code로 주문 번호 가져오기
//    public int getOrderNoByPaidCode(String paid_code);
//    // paid_code로 포인트 차감
//    public void updateUserPointByPaidCode(String paid_code);
//}
