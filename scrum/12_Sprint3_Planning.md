# Sprint 3 · Planning

**Objetivo:** consolidar el módulo de inventario (CRUD artículos, alertas)
y exponer la API documentada.

| HU    | Historia                                       | Pts | Estado |
|-------|------------------------------------------------|-----|--------|
| HU-07 | CRUD de artículos en Spring Boot               | 6   | ✅ Implementado en `ArticuloService` / `ArticuloWebController` |
| HU-08 | Dashboard con alertas de stock                 | 7   | ✅ `dashboards/*.html` con tarjeta "Bajo stock" |
| HU-09 | Documentación Swagger / OpenAPI                | 6   | ✅ `springdoc-openapi` → `/swagger-ui.html` |

## DoD
- Endpoints REST visibles en `/swagger-ui.html`.
- Dashboard muestra contador de artículos con `stockActual <= stockMinimo`.
- ABM de artículos disponible para ADMIN y ALMACENISTA.
