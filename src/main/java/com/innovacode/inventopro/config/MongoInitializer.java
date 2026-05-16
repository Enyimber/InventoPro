package com.innovacode.inventopro.config;

import com.innovacode.inventopro.model.*;
import com.innovacode.inventopro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Inicializa MongoDB al arranque:
 *  - Crea las colecciones si no existen.
 *  - Crea/repara los índices (maneja conflictos de nombres).
 *  - Inserta el usuario administrador por defecto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${inventopro.admin.username}") private String adminUser;
    @Value("${inventopro.admin.password}") private String adminPass;
    @Value("${inventopro.admin.email}")    private String adminEmail;

    private static final String[] COLECCIONES = {
        "usuarios", "categorias", "articulos", "proveedores", "movimientos", "auditoria"
    };

    @Override
    public void run(String... args) {
        log.info("====================================");
        log.info("INICIALIZANDO MONGODB");
        log.info("====================================");
        crearColecciones();
        crearIndices();
        crearAdminPorDefecto();
        log.info("====================================");
        log.info("BASE DE DATOS LISTA");
        log.info("====================================");
    }

    private void crearColecciones() {
        for (String c : COLECCIONES) {
            if (!mongoTemplate.collectionExists(c)) {
                mongoTemplate.createCollection(c);
                log.info("Colección creada: {}", c);
            } else {
                log.info("Colección existente: {}", c);
            }
        }
    }

    private void crearIndices() {
        // Artículos
        ensureIndex(Articulo.class, "codigo", "codigo_unique",
                new Index().on("codigo", Sort.Direction.ASC).unique().named("codigo_unique"));
        ensureIndex(Articulo.class, "nombre", "nombre_idx",
                new Index().on("nombre", Sort.Direction.ASC).named("nombre_idx"));
        // Movimientos
        ensureIndex(Movimiento.class, "fecha", "fecha_desc",
                new Index().on("fecha", Sort.Direction.DESC).named("fecha_desc"));
        // Usuarios
        ensureIndex(Usuario.class, "username", "username_unique",
                new Index().on("username", Sort.Direction.ASC).unique().named("username_unique"));
        ensureIndex(Usuario.class, "email", "email_unique",
                new Index().on("email", Sort.Direction.ASC).unique().named("email_unique"));

        log.info("Índices verificados correctamente");
    }

    /**
     * Asegura un índice sobre `field` con `wantedName`. Si existe otro índice
     * sobre el mismo campo con un nombre distinto (caso típico al cambiar de
     * Atlas o crear índices manualmente), lo elimina y lo recrea con el nombre
     * deseado para evitar IndexOptionsConflict.
     */
    private void ensureIndex(Class<?> entityClass, String field, String wantedName, Index newIndex) {
        try {
            IndexOperations ops = mongoTemplate.indexOps(entityClass);
            List<IndexInfo> existing = ops.getIndexInfo();
            for (IndexInfo info : existing) {
                boolean sameField = info.getIndexFields().stream()
                        .anyMatch(f -> f.getKey().equals(field));
                if (sameField && !info.getName().equals(wantedName) && !"_id_".equals(info.getName())) {
                    try {
                        ops.dropIndex(info.getName());
                        log.info("Índice antiguo eliminado: {} (campo {})", info.getName(), field);
                    } catch (Exception ex) {
                        log.warn("No se pudo eliminar índice {}: {}", info.getName(), ex.getMessage());
                    }
                }
            }
            ops.ensureIndex(newIndex);
            log.info("Índice creado: {}", wantedName);
        } catch (Exception e) {
            log.warn("Error verificando índice {} sobre {}: {}", wantedName, field, e.getMessage());
        }
    }

    private void crearAdminPorDefecto() {
        if (usuarioRepository.existsByUsername(adminUser)) {
            log.info("Admin '{}' ya existe", adminUser);
            return;
        }
        Usuario admin = Usuario.builder()
            .username(adminUser)
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPass))
            .nombreCompleto("Administrador InventoPro")
            .roles(Set.of("ADMIN"))
            .activo(true)
            .creadoEn(LocalDateTime.now())
            .build();
        usuarioRepository.save(admin);
        log.info("====================================");
        log.info("ADMIN CREADO");
        log.info("Usuario: {}", adminUser);
        log.info("Password: {}", adminPass);
    }
}
