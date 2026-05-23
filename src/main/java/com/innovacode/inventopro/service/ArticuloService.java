package com.innovacode.inventopro.service;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.repository.ArticuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloService {
    private final ArticuloRepository repo;

    public List<Articulo> listar() { return repo.findAll(); }
    public Articulo obtener(String id) { return repo.findById(id).orElseThrow(); }

    public Articulo crear(Articulo a) {
        a.setCreadoEn(LocalDateTime.now());
        a.setActualizadoEn(LocalDateTime.now());
        a.setActivo(true);
        return repo.save(a);
    }

    public Articulo actualizar(String id, Articulo a) {
        Articulo existente = obtener(id);
        existente.setNombre(a.getNombre());
        existente.setCategoria(a.getCategoria());
        existente.setMarca(a.getMarca());
        existente.setUnidadMedida(a.getUnidadMedida());
        existente.setStockActual(a.getStockActual());
        existente.setStockMinimo(a.getStockMinimo());
        existente.setPrecioUnitario(a.getPrecioUnitario());
        existente.setActualizadoEn(LocalDateTime.now());
        return repo.save(existente);
    }

    public void eliminar(String id) { repo.deleteById(id); }
    public List<Articulo> bajoStock() { return repo.findBajoStockMinimo(); }
    public List<Articulo> buscar(String q) { return repo.findByNombreContainingIgnoreCase(q); }
}
