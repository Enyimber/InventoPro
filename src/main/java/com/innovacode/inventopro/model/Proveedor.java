package com.innovacode.inventopro.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proveedores")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Proveedor {
    @Id private String id;
    @Indexed(unique = true) private String nit;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String email;
    private boolean activo;
}
