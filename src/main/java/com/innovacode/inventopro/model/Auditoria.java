package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "auditoria")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Auditoria {
    @Id private String id;
    private String entidad;
    private String entidadId;
    private String accion;
    private String usuario;
    private String datosAnteriores;
    private String datosNuevos;
    private LocalDateTime fecha;
}
