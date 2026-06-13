# Sprint 4 · Planning

**Objetivo:** cerrar el aseguramiento de calidad y la entrega de reportes.

| HU    | Historia                                  | Pts | Estado |
|-------|-------------------------------------------|-----|--------|
| HU-10 | Pruebas de integración con Testcontainers | 6   | ⏳ Pendiente (esqueleto en `src/test`) |
| HU-11 | Burndown chart en tiempo real             | 6   | ✅ Chart.js en dashboard ADMIN |
| HU-12 | **Exportación PDF / Excel**               | 4   | ✅ Módulo `/reportes` (OpenPDF + Apache POI) |

## Detalle HU-12 (entregado)
- `ReporteService` genera 3 reportes en ambos formatos:
  artículos, alertas de bajo stock y movimientos.
- `ReporteController` expone:
  - `GET /reportes/articulos.pdf|xlsx`
  - `GET /reportes/bajo-stock.pdf|xlsx`
  - `GET /reportes/movimientos.pdf|xlsx`
- Vista `/reportes` con tarjetas de descarga.
- Acceso restringido por `@PreAuthorize("hasAnyRole('ADMIN','GERENTE','AUDITOR')")`.

## DoD
- PDFs con encabezado corporativo y filas alternadas.
- Excel con autosize de columnas y encabezado azul.
- Botones visibles en el sidebar para roles permitidos.
