package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioApiController {
    private final UsuarioService service;

    @GetMapping public List<Usuario> listar() { return service.listar(); }

    @PostMapping
    public Usuario crear(@RequestBody CrearUsuarioRequest req) {
        Usuario u = Usuario.builder()
            .username(req.username()).email(req.email())
            .nombreCompleto(req.nombreCompleto()).roles(req.roles()).build();
        return service.crear(u, req.password());
    }

    public record CrearUsuarioRequest(String username, String email, String password,
                                      String nombreCompleto, java.util.Set<String> roles) {}
}
