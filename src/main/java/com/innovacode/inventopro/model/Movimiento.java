package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "movimientos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Movimiento {
    @Id private String id;
    @Indexed private String articuloId;
    private String tipo;       // ENTRADA / SALIDA
    private int cantidad;
    private double precioUnitario;
    private String proveedorId;
    private String areaDestino;
    private String motivo;
    private String usuario;
    @Indexed private LocalDateTime fecha;
}
