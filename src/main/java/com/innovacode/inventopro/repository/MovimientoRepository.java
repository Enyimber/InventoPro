package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Movimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends MongoRepository<Movimiento, String> {
    List<Movimiento> findByProveedorIdOrderByFechaDesc(String proveedorId);
    List<Movimiento> findByUsuarioOrderByFechaDesc(String usuario);
    List<Movimiento> findByTipoAndFechaBetween(String tipo, LocalDateTime ini, LocalDateTime fin);
    List<Movimiento> findByFechaBetween(LocalDateTime ini, LocalDateTime fin);
    List<Movimiento> findByEstadoOrderByFechaDesc(String estado);
}
