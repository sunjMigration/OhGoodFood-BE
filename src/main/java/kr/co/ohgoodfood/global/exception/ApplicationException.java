package kr.co.ohgoodfood.global.exception;

import org.springframework.http.HttpStatus;

/**
 * ApplicationException.java
 *
 * 애플리케이션 전반에서 쓰이는 예외의 공통 베이스 클래스.
 * errorCode나 httpStatus 같은 공통 필드를 여기 정의하고, 개별 예외는 메세지만 넘겨주도록 한다.
 */
public abstract class ApplicationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    protected ApplicationException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    //예외 체이닝 생성자 추가
    protected ApplicationException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
