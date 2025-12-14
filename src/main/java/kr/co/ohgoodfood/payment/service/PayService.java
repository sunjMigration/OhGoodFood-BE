package kr.co.ohgoodfood.payment.service;

public interface PayService {
    // 주문,결제 정보 삽입
    public boolean insertOrderAndPaid(String user_id, String store_id, int product_no, int quantity, int paid_price, String orderId, int paid_point);
    // 주문,결제 상태 업데이트
    public boolean updateOrderStatusAndPaidStatus(String orderId);
    // 상품 재고에서 주문 개수만큼 차감 가능 여부 확인
    public boolean checkProductAmount(int product_no, int quantity);
    // piad_code로 상품 재고에서 주문 개수만큼 차감 가능 여부 확인
    public boolean checkProductAmountByPaidCode(String paid_code);
    // 결제 실패 시 주문 canceld_from 업데이트
    public boolean updateOrderCanceldFromByPaidCode(String paid_code);
    // 결제 실패 시 주문 canceld_from 가져오기
    public String getOrderCanceldFromByPaidCode(String paid_code);
    // 가게 상태 확인하기 
    public boolean getStoreStatus(String store_id);
    // Paid_code로 가게 상태 확인하기
    public boolean getStoreStatusByPaidCode(String paid_code);
    // Paid_code로 주문 번호 가져오기
    public int getOrderNoByPaidCode(String paid_code);
    // Paid_code로 포인트 차감
    public boolean updateUserPointByPaidCode(String paid_code);
}
