package com.innovacode.inventopro.service;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.model.Movimiento;
import com.innovacode.inventopro.repository.ArticuloRepository;
import com.innovacode.inventopro.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {
    private final MovimientoRepository repo;
    private final ArticuloRepository articuloRepo;

    public List<Movimiento> listar() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }

    /** Registra una ENTRADA o SALIDA y ajusta el stock del artículo. */
    public Movimiento registrar(Movimiento m, String usuario) {
        Articulo a = articuloRepo.findById(m.getArticuloId())
            .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado"));

        String tipo = m.getTipo() == null ? "" : m.getTipo().toUpperCase();
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA"))
            throw new IllegalArgumentException("Tipo debe ser ENTRADA o SALIDA");
        if (m.getCantidad() <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        if (tipo.equals("ENTRADA")) {
            a.setStockActual(a.getStockActual() + m.getCantidad());
        } else {
            if (a.getStockActual() < m.getCantidad())
                throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + a.getStockActual());
            a.setStockActual(a.getStockActual() - m.getCantidad());
        }
        a.setActualizadoEn(LocalDateTime.now());
        articuloRepo.save(a);

        m.setTipo(tipo);
        m.setUsuario(usuario);
        m.setFecha(LocalDateTime.now());
        m.setEstado("ACEPTADO");
        return repo.save(m);
    }

    /** Registra una entrega de proveedor como PENDIENTE sin modificar el stock. */
    public Movimiento registrarPendiente(Movimiento m, String usuario) {
        articuloRepo.findById(m.getArticuloId())
            .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado"));

        if (m.getCantidad() <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        m.setTipo("ENTRADA");
        m.setUsuario(usuario);
        m.setFecha(LocalDateTime.now());
        m.setEstado("PENDIENTE");
        return repo.save(m);
    }

    /** Procesa una entrega pendiente, aceptándola (actualiza stock y cambia estado a ACEPTADO) o rechazándola (cambia estado a RECHAZADO). */
    public Movimiento procesarEntrega(String id, boolean aceptar, String usuarioProcesador) {
        Movimiento m = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada"));

        if (!"PENDIENTE".equals(m.getEstado())) {
            throw new IllegalStateException("El movimiento no está en estado PENDIENTE");
        }

        if (aceptar) {
            Articulo a = articuloRepo.findById(m.getArticuloId())
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado"));
            a.setStockActual(a.getStockActual() + m.getCantidad());
            a.setActualizadoEn(LocalDateTime.now());
            articuloRepo.save(a);
            m.setEstado("ACEPTADO");
        } else {
            m.setEstado("RECHAZADO");
        }
        
        m.setUsuario(usuarioProcesador);
        return repo.save(m);
    }
}
