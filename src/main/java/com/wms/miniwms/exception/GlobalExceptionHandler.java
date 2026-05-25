package com.wms.miniwms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 프로젝트 전역에서 발생하는 예외를 JSON 형태로 가로채는 어노테이션
public class GlobalExceptionHandler {

    // IllegalArgumentException이 발생했을 때 가로채는 메서드
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errors = new HashMap<>();

        // 엔티티나 서비스에서 throw new IllegalArgumentException("메시지")에 넣었던 문구가 e.getMessage()로 돌아옵니다.
        errors.put("error", "Bad Request");
        errors.put("message", e.getMessage());

        // 클라이언트에게 서버 잘못(500)이 아닌, 잘못된 요청(400 Bad Request) 상태 코드와 함께 깔끔한 JSON을 반환합니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // DTO의 @Valid 검증(예: @Min, @NotBlank 등)에서 실패했을 때 가로채는 메서드
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        // DTO에 적어둔 꼼꼼한 message 내용("출고 수량은 최소 1개 이상이어야 합니다.")을 추출합니다.
        String defaultMessage = e.getBindingResult().getFieldError().getDefaultMessage();

        errors.put("error", "Bad Request");
        errors.put("message", defaultMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
