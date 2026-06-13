package com.innovacode.inventopro.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Reemplaza la "Whitelabel Error Page" por una vista propia.
 * - /error  (HTML)  -> templates/error.html  (para el aplicativo web)
 * - /error  (JSON)  -> respuesta JSON        (para los controladores /api/**)
 */
@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String errorHtml(HttpServletRequest req, Model model) {
        int status = statusCode(req);
        model.addAttribute("status", status);
        model.addAttribute("error",  HttpStatus.resolve(status) != null
                ? HttpStatus.valueOf(status).getReasonPhrase() : "Error");
        model.addAttribute("path",    attr(req, RequestDispatcher.ERROR_REQUEST_URI));
        model.addAttribute("message", attr(req, RequestDispatcher.ERROR_MESSAGE));
        Throwable ex = (Throwable) req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        model.addAttribute("exception", ex != null ? ex.getClass().getName() : null);
        return "error";
    }

    @RequestMapping
    public ResponseEntity<Map<String,Object>> errorJson(HttpServletRequest req) {
        int status = statusCode(req);
        Map<String,Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", HttpStatus.resolve(status) != null
                ? HttpStatus.valueOf(status).getReasonPhrase() : "Error");
        body.put("path",    attr(req, RequestDispatcher.ERROR_REQUEST_URI));
        body.put("message", attr(req, RequestDispatcher.ERROR_MESSAGE));
        return ResponseEntity.status(status).body(body);
    }

    private int statusCode(HttpServletRequest req) {
        Object s = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return s == null ? 500 : Integer.parseInt(s.toString());
    }
    private Object attr(HttpServletRequest req, String name) { return req.getAttribute(name); }
}
