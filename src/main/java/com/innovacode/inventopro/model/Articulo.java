package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "articulos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Articulo {
    @Id private String id;
    @Indexed(unique = true) private String codigo;
    @Indexed private String nombre;
    private String categoria;
    private String marca;
    private String unidadMedida;
    private int stockActual;
    private int stockMinimo;
    private double precioUnitario;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
