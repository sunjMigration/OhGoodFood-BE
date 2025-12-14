package kr.co.ohgoodfood.global.exception;

import org.springframework.http.HttpStatus;

/**
 * OrderCancelException.java
 *
 * order cancel시 발생하는 예외 클래스
 * errorCode나 httpStatus 같은 공통 필드를 여기 정의하고, 개별 예외는 메세지만 넘겨주도록 한다.
 * transaction rollback이 필요한 경우에 사용된다.
 */

public class OrderCancelException extends ApplicationException {
    //ERROR_CODE 상수
    private static final String ERROR_CODE = "ORDER_CANCEL_ERROR";

    public OrderCancelException(int updateOrderCnt, int updateAmountCnt) {
        super(
                ERROR_CODE,
                formatMessage(updateOrderCnt, updateAmountCnt),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    //에러 메세지 보존
    public OrderCancelException(int updateOrderCnt, int updateAmountCnt, Throwable cause) {
        super(
                ERROR_CODE,
                formatMessage(updateOrderCnt, updateAmountCnt),
                HttpStatus.INTERNAL_SERVER_ERROR,
                cause
        );
    }

    private static String formatMessage(int updateOrderCnt, int updateAmountCnt) {
        return String.format(
                "주문 취소 중 오류가 발생했습니다. DB를 확인해주세요: updateOrderCnt=%d, updateAmountCnt=%d" ,
                updateOrderCnt, updateAmountCnt
        );
    }
}
