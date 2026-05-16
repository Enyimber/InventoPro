package com.innovacode.inventopro.security;

import java.util.List;

/**
 * Roles del sistema InventoPro. ADMIN tiene permisos totales.
 */
public final class Roles {
    public static final String ADMIN        = "ADMIN";        // control total
    public static final String GERENTE      = "GERENTE";      // reportes + aprobaciones
    public static final String ALMACENISTA  = "ALMACENISTA";  // entradas/salidas, stock
    public static final String VENDEDOR     = "VENDEDOR";     // registra salidas
    public static final String COMPRADOR    = "COMPRADOR";    // registra entradas
    public static final String AUDITOR      = "AUDITOR";      // sólo lectura + auditoría
    public static final String CONSULTOR    = "CONSULTOR";    // sólo lectura

    public static final List<String> TODOS = List.of(
        ADMIN, GERENTE, ALMACENISTA, VENDEDOR, COMPRADOR, AUDITOR, CONSULTOR
    );

    private Roles() {}
}
