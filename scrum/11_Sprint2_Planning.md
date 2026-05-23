# Sprint 2 · Planning

**Objetivo del Sprint**
Entregar los módulos de **Usuario Almacenista** y **Proveedor**, e implementar
dashboards diferenciados (estilo Snipe-IT) para los 7 roles oficiales:
Administrador, Almacenista, Auditor, Proveedor, Gerente, Supervisor de Área
y Comprador.

## Historias de usuario incluidas

| ID  | Como…        | Quiero…                                                      | Estimación |
|-----|--------------|--------------------------------------------------------------|------------|
| US-08 | Almacenista | Tener un panel con acciones rápidas (entrada/salida)         | 3 pts |
| US-09 | Almacenista | Ver alertas de bajo stock y movimientos del día              | 2 pts |
| US-10 | Proveedor   | Acceder a un portal con solo mis entregas                    | 3 pts |
| US-11 | Proveedor   | Registrar nuevas entregas (que generen ENTRADA en almacén)   | 5 pts |
| US-12 | Admin       | Vincular un usuario PROVEEDOR con un proveedor del catálogo  | 2 pts |
| US-13 | Todos       | Ver un dashboard distinto según mi rol con gráficas          | 5 pts |
| US-14 | Gerente     | Ver gráficas de movimientos últimos 7 días y top 5 artículos | 3 pts |
| US-15 | Auditor     | Tener vista solo-lectura con accesos rápidos a reportes      | 2 pts |
| US-16 | Supervisor  | Ver alertas y acciones de aprobación de su área              | 3 pts |
| US-17 | Comprador   | Crear órdenes de compra (ENTRADA) desde su panel             | 3 pts |

## Definición de Hecho (DoD)
- Cada rol tiene su propio template `dashboards/<rol>.html`.
- `DashboardController` dispatcha por rol principal del usuario logueado.
- Gráficas renderizadas con Chart.js (líneas + barras).
- Usuario con rol PROVEEDOR sólo ve `/proveedor/**`.
- Seguridad por rol verificada con `@PreAuthorize`.
