package com.innovacode.inventopro.service;

import com.innovacode.inventopro.model.Proveedor;
import com.innovacode.inventopro.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository repo;

    public List<Proveedor> listar() { return repo.findAll(); }

    public Proveedor obtener(String id) {
        return repo.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Proveedor no encontrado: " + id));
    }

    public Proveedor crear(Proveedor p) {
        p.setId(null);
        p.setActivo(true);
        return repo.save(p);
    }

    public Proveedor actualizar(String id, Proveedor datos) {
        Proveedor p = obtener(id);
        p.setNit(datos.getNit());
        p.setRazonSocial(datos.getRazonSocial());
        p.setContacto(datos.getContacto());
        p.setTelefono(datos.getTelefono());
        p.setEmail(datos.getEmail());
        return repo.save(p);
    }

    public Proveedor toggleActivo(String id) {
        Proveedor p = obtener(id);
        p.setActivo(!p.isActivo());
        return repo.save(p);
    }

    public void eliminar(String id) { repo.deleteById(id); }
}
