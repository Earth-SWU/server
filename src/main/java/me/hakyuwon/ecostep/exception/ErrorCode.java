package me.hakyuwon.ecostep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 등록된 이메일입니다."),
    DUPLICATE_PHONE(HttpStatus.BAD_REQUEST, "이미 등록된 전화번호입니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "전화번호 형식이 잘못되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),

    // 401 Unauthorized
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다."),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    FILE_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
