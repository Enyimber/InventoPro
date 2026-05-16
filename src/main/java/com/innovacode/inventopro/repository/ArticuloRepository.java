package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Articulo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ArticuloRepository extends MongoRepository<Articulo, String> {
    List<Articulo> findByNombreContainingIgnoreCase(String nombre);
    List<Articulo> findByCategoria(String categoria);
    @Query("{ $expr: { $lt: [ '$stockActual', '$stockMinimo' ] } }")
    List<Articulo> findBajoStockMinimo();
}
