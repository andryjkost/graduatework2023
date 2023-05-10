package ru.graduatework.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CommonException extends RuntimeException {

    private final Code code;
    private final String userMessage;
    private final String techMessage;
    private final HttpStatus httpStatus;
}
