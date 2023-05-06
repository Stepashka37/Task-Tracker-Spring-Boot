package com.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({TaskNotFoundException.class, SubtaskNotFoundException.class, EpicNotFoundException.class})

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse userNotFoundExc(final RuntimeException p) {
        log.info("404: " + p.getMessage());
        return new ExceptionResponse("Объект не найден", p.getMessage());
    }

}
