package org.luismore.hlvsapi.handlers;

import lombok.extern.slf4j.Slf4j;
import org.luismore.hlvsapi.domain.dtos.GeneralResponse;
import org.luismore.hlvsapi.utils.ErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {
    private final ErrorMapper errorMapper;

    public GlobalErrorHandler(ErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<GeneralResponse> GeneralExceptionHandler (Exception e) {
        log.error(e.getMessage(), e);
        return GeneralResponse.getResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<GeneralResponse> NotFoundExceptionHandler (NoResourceFoundException e) {
        return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<GeneralResponse> BadRequestExceptionHandler(MethodArgumentNotValidException e) {
        return GeneralResponse.getResponse(
                HttpStatus.BAD_REQUEST,
                errorMapper.map(e.getBindingResult().getFieldErrors())
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    private ResponseEntity<GeneralResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return GeneralResponse.getResponse(HttpStatus.FORBIDDEN, "Access Denied");
    }
}
