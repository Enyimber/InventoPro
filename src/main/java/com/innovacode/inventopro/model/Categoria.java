package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Categoria {
    @Id private String id;
    @Indexed(unique = true) private String nombre;
    private String descripcion;
}
