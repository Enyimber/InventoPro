package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Proveedor;
import com.innovacode.inventopro.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProveedorWebController {

    private final ProveedorService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", service.listar());
        return "proveedores/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", Proveedor.builder().activo(true).build());
        model.addAttribute("modo", "crear");
        return "proveedores/form";
    }

    @PostMapping
    public String crear(@ModelAttribute Proveedor proveedor, RedirectAttributes ra) {
        try {
            service.crear(proveedor);
            ra.addFlashAttribute("ok", "Proveedor creado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo crear: " + e.getMessage());
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("proveedor", service.obtener(id));
        model.addAttribute("modo", "editar");
        return "proveedores/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id,
                             @ModelAttribute Proveedor proveedor,
                             RedirectAttributes ra) {
        try {
            service.actualizar(id, proveedor);
            ra.addFlashAttribute("ok", "Proveedor actualizado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo actualizar: " + e.getMessage());
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable String id, RedirectAttributes ra) {
        Proveedor p = service.toggleActivo(id);
        ra.addFlashAttribute("ok", "Proveedor " + (p.isActivo() ? "activado" : "desactivado"));
        return "redirect:/proveedores";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes ra) {
        service.eliminar(id);
        ra.addFlashAttribute("ok", "Proveedor eliminado");
        return "redirect:/proveedores";
    }
}
