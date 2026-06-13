package com.innovacode.inventopro.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Manejo global de errores:
 *  - Para /api/**  -> JSON
 *  - Para el resto -> vista 'error' (Thymeleaf)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleAll(Exception ex, HttpServletRequest req) {
        log.error("Error no controlado en {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        boolean isApi = req.getRequestURI() != null && req.getRequestURI().startsWith("/api/");
        HttpStatus status = (ex instanceof AccessDeniedException)
                ? HttpStatus.FORBIDDEN : HttpStatus.INTERNAL_SERVER_ERROR;

        if (isApi) {
            return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error",  status.getReasonPhrase(),
                "message", ex.getMessage() == null ? "Error interno" : ex.getMessage(),
                "path",    req.getRequestURI()
            ));
        }

        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        mav.addObject("status", status.value());
        mav.addObject("error",  status.getReasonPhrase());
        mav.addObject("path",   req.getRequestURI());
        mav.addObject("message", ex.getMessage());
        mav.addObject("exception", ex.getClass().getName());
        return mav;
    }
}
