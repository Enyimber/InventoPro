package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.security.Roles;
import com.innovacode.inventopro.service.ProveedorService;
import com.innovacode.inventopro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioWebController {

    private final UsuarioService service;
    private final ProveedorService proveedorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario",
            Usuario.builder().roles(new HashSet<>()).activo(true).build());
        model.addAttribute("rolesDisponibles", Roles.TODOS);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("modo", "crear");
        return "usuarios/form";
    }

    @PostMapping
    public String crear(@ModelAttribute Usuario usuario,
                        @RequestParam String passwordPlano,
                        RedirectAttributes ra) {
        try {
            if (usuario.getRoles() == null || usuario.getRoles().isEmpty())
                throw new IllegalArgumentException("Debes asignar al menos un rol");
            service.crear(usuario, passwordPlano);
            ra.addFlashAttribute("ok", "Usuario creado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo crear: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("usuario", service.obtener(id));
        model.addAttribute("rolesDisponibles", Roles.TODOS);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("modo", "editar");
        return "usuarios/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
                             @ModelAttribute Usuario usuario,
                             @RequestParam(required = false) String passwordPlano,
                             RedirectAttributes ra) {
        try {
            if (usuario.getRoles() == null || usuario.getRoles().isEmpty())
                throw new IllegalArgumentException("Debes asignar al menos un rol");
            service.actualizar(id, usuario, passwordPlano);
            ra.addFlashAttribute("ok", "Usuario actualizado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo actualizar: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable String id, RedirectAttributes ra) {
        Usuario u = service.toggleActivo(id);
        ra.addFlashAttribute("ok", "Usuario " + (u.isActivo() ? "activado" : "desactivado"));
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Usuario eliminado");
        return "redirect:/usuarios";
    }
}
