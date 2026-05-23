package com.innovacode.inventopro.service;

import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public List<Usuario> listar() { return repo.findAll(); }

    public Usuario obtener(String id) {
        return repo.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    public Usuario crear(Usuario u, String passwordPlano) {
        u.setPassword(encoder.encode(passwordPlano));
        if (u.getRoles() == null || u.getRoles().isEmpty()) u.setRoles(Set.of("ALMACENISTA"));
        u.setCreadoEn(LocalDateTime.now());
        return repo.save(u);
    }

    public Usuario actualizar(String id, Usuario datos, String passwordPlano) {
        Usuario u = obtener(id);
        u.setUsername(datos.getUsername());
        u.setNombreCompleto(datos.getNombreCompleto());
        u.setEmail(datos.getEmail());
        u.setActivo(datos.isActivo());
        if (datos.getRoles() != null && !datos.getRoles().isEmpty()) {
            u.setRoles(datos.getRoles());
        }
        if (passwordPlano != null && !passwordPlano.isBlank()) {
            u.setPassword(encoder.encode(passwordPlano));
        }
        return repo.save(u);
    }

    public Usuario toggleActivo(String id) {
        Usuario u = obtener(id);
        u.setActivo(!u.isActivo());
        return repo.save(u);
    }

    public void eliminar(String id) { repo.deleteById(id); }
}
