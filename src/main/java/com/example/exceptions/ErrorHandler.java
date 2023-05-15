package com.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler({TaskNotFoundException.class, SubtaskNotFoundException.class, EpicNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse taskNotFoundExc(final RuntimeException p) {
        log.info("404: " + p.getMessage());
        return new ExceptionResponse("Объект не найден", p.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse annotationValidationExc(MethodArgumentNotValidException exc) {
        log.info("400: " + exc.getMessage());
        return new ExceptionResponse("Ошибка валидации с помощью аннотаций", exc.getMessage());
    }

}
