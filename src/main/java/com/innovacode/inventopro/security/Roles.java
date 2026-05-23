package com.innovacode.inventopro.security;

import java.util.List;

/**
 * Los 7 roles oficiales de InventoPro (según documento de requerimientos).
 * Se mantienen VENDEDOR y CONSULTOR como aliases internos para no romper
 * datos previos, pero la UI sólo expone los 7 oficiales.
 */
public final class Roles {
    public static final String ADMIN       = "ADMIN";        // 1. Control total
    public static final String ALMACENISTA = "ALMACENISTA";  // 2. Entradas/salidas
    public static final String AUDITOR     = "AUDITOR";      // 3. Consulta y reportes
    public static final String PROVEEDOR   = "PROVEEDOR";    // 4. Gestiona sus entregas
    public static final String GERENTE     = "GERENTE";      // 5. Dashboards e informes
    public static final String SUPERVISOR  = "SUPERVISOR";   // 6. Aprueba solicitudes de su área
    public static final String COMPRADOR   = "COMPRADOR";    // 7. Órdenes de compra

    // Compatibilidad histórica
    public static final String VENDEDOR    = "VENDEDOR";
    public static final String CONSULTOR   = "CONSULTOR";

    public static final List<String> TODOS = List.of(
        ADMIN, ALMACENISTA, AUDITOR, PROVEEDOR, GERENTE, SUPERVISOR, COMPRADOR
    );

    private Roles() {}
}
