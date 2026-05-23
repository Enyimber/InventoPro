package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "usuarios")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id private String id;
    @Indexed(unique = true) private String username;
    @Indexed(unique = true) private String email;
    private String password;
    private String nombreCompleto;
    private Set<String> roles;
    /** Para usuarios con rol PROVEEDOR: id del proveedor que representa. */
    private String proveedorId;
    /** Para usuarios con rol SUPERVISOR: área que supervisa. */
    private String area;
    private boolean activo;
    private LocalDateTime creadoEn;
}
