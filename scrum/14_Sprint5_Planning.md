# Sprint 5 · Planning (cierre)

**Objetivo:** estabilización, despliegue y entrega final.

| Tarea                                          | Estado |
|------------------------------------------------|--------|
| Hardening de seguridad (revisión RoleHierarchy)| ✅ |
| Auditoría de accesos (`Auditoria` collection)  | ✅ Modelo + repositorio listos |
| Dashboards diferenciados (7 roles)             | ✅ Sprint 2 |
| Reportes PDF / Excel                           | ✅ Sprint 4 |
| Documentación Swagger                          | ✅ Sprint 3 |
| Dockerfile para despliegue                     | ✅ Incluido en raíz |
| Manual de usuario (README.md)                  | ✅ |
| Demo final y handover                          | 🟡 Programado |

## Checklist de entrega
- [x] Build `mvn clean package` sin errores.
- [x] `docker build -t inventopro .` funcional.
- [x] Seeders de datos demo (`MongoInitializer`).
- [x] Credenciales documentadas (`admin/admin123`, etc.).
- [x] Endpoints de exportación probados desde el navegador.
