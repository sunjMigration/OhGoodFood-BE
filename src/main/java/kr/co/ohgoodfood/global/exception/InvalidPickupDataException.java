package kr.co.ohgoodfood.global.exception;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class InvalidPickupDataException extends ApplicationException {

    //ERROR_CODE 상수
    private static final String ERROR_CODE = "PICKUP_DATA_INVALID";

    public InvalidPickupDataException(String storeStatus, Timestamp pickupStart, int amount) {
        super(
                ERROR_CODE,
                formatMessage(storeStatus, pickupStart, amount),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    //에러 메세지 보존
    public InvalidPickupDataException(String storeStatus, Timestamp pickupStart, int amount, Throwable cause) {
        super(
                ERROR_CODE,
                formatMessage(storeStatus, pickupStart, amount),
                HttpStatus.INTERNAL_SERVER_ERROR,
                cause
        );
    }

    private static String formatMessage(String storeStatus, Timestamp pickupStart, int amount) {
        return String.format(
                "픽업 데이터 상태가 올바르지 않습니다. DB를 확인해주세요: store_status=%s, pickup_start=%s, amount=%d",
                storeStatus, pickupStart, amount
        );
    }
}
