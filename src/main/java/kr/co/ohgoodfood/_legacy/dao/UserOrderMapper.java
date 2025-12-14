//package kr.co.ohgoodfood._legacy.dao;
//
//import kr.co.ohgoodfood._legacy.dto.UserOrderDTO;
//import kr.co.ohgoodfood._legacy.dto.UserOrderFilter;
//import kr.co.ohgoodfood._legacy.dto.UserOrderRequest;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
///**
// * UserOrderMapper
// *
// * 사용자 order 페이지에서 사용하는 mapper interface
// */
//@Mapper
//public interface UserOrderMapper {
//    /**
//     * 사용자의 모든 주문내역을 출력
//     *
//     * @param userOrderFilter  필터 DTO
//     * @return                 UserOrder 리스트
//     */
//    List<UserOrderDTO> selectOrderList(@Param("filter") UserOrderFilter userOrderFilter);
//
//    /**
//     * 사용자가 주문 상태를 변경해야 할때 사용한다.
//     *
//     * @param userOrderRequest 필터 DTO
//     */
//    int updateOrderStatus(@Param("order_request") UserOrderRequest userOrderRequest);
//
//    /**
//     * 사용자 주문 취소시, Product의 amount를 복원하기 위함이다.
//     *
//     * @param userOrderRequest 필터 DTO
//     */
//    int restoreProductAmount(@Param("order_request") UserOrderRequest userOrderRequest);
//
//}
