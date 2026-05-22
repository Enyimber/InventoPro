package com.innovacode.inventopro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;
import java.util.stream.Collectors;

/** Inyecta atributos comunes (rol principal) en todas las vistas. */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("rolPrincipal")
    public String rolPrincipal(Authentication auth) {
        if (auth == null) return "";
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        String[] orden = {"ROLE_ADMIN","ROLE_GERENTE","ROLE_AUDITOR","ROLE_SUPERVISOR",
                          "ROLE_ALMACENISTA","ROLE_COMPRADOR","ROLE_PROVEEDOR"};
        for (String r : orden) if (roles.contains(r)) return r.substring(5);
        return "USUARIO";
    }
}
