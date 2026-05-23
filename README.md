# 📦 InventoPro — Sistema Gestor de Inventario

**Stack:** Spring Boot 3.3 · Java 21 · MongoDB Atlas · Spring Security · Thymeleaf · SpringDoc OpenAPI

---

## 🚀 Cómo abrir el proyecto en Spring Tools (STS)

1. Descomprime el proyecto.
2. Abre **Spring Tools Suite** → `File → Import → Existing Maven Projects`.
3. Selecciona la carpeta `inventopro`.
4. Espera a que Maven descargue todas las dependencias.
5. Botón derecho sobre `InventoproApplication.java` → `Run As → Spring Boot App`.

## 🔌 Conectar a MongoDB Atlas

Edita `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://USUARIO:PASSWORD@cluster0.xxxxx.mongodb.net
spring.data.mongodb.database=inventopro
```

> O define las variables de entorno `MONGODB_URI` y `MONGODB_DB`.

### ✨ Auto-creación al cambiar de base
Al iniciar la aplicación, `MongoInitializer` automáticamente:
- Crea las **6 colecciones**: `usuarios`, `articulos`, `categorias`, `proveedores`, `movimientos`, `auditoria`.
- Crea los **índices** únicos y de búsqueda.
- Crea el **usuario admin** por defecto si no existe.

> Cambia la URI a otro cluster y todo se vuelve a crear sin intervención manual.

## 🔑 Credenciales por defecto

| Usuario | Contraseña |
|---|---|
| `admin` | `Admin123*` |

Configurable en `application.properties` (`inventopro.admin.username` / `inventopro.admin.password`).

## 🌐 URLs

| Endpoint | Descripción |
|---|---|
| `http://localhost:8080/login` | Login web |
| `http://localhost:8080/dashboard` | Dashboard |
| `http://localhost:8080/articulos` | CRUD web de artículos |
| `http://localhost:8080/swagger-ui.html` | API REST documentada |
| `http://localhost:8080/api/articulos` | Endpoint REST de artículos |

## 🗂️ Estructura

```
src/main/java/com/innovacode/inventopro/
├── InventoproApplication.java
├── config/         → MongoInitializer, SecurityConfig
├── model/          → Usuario, Articulo, Categoria, Proveedor, Movimiento, Auditoria
├── repository/     → Un repositorio por entidad
├── service/        → Lógica de negocio
├── security/       → UsuarioDetailsService
└── controller/     → ⚠️ Web + REST en la MISMA carpeta, archivos separados
    ├── HomeController.java
    ├── DashboardController.java
    ├── ArticuloWebController.java     ← UI Thymeleaf
    ├── ArticuloApiController.java     ← REST API
    ├── UsuarioWebController.java
    └── UsuarioApiController.java
```

## 📊 Documentación Scrum
Carpeta `scrum/` contiene los 11 artefactos del Sprint 1.
