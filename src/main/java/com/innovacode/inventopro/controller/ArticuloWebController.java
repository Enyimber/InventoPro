package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.service.ArticuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/articulos")
@RequiredArgsConstructor
public class ArticuloWebController {
    private final ArticuloService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("articulos", service.listar());
        return "articulos/list";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("articulo", new Articulo());
        return "articulos/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Articulo articulo) {
        service.crear(articulo);
        return "redirect:/articulos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("articulo", service.obtener(id));
        return "articulos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute Articulo articulo) {
        service.actualizar(id, articulo);
        return "redirect:/articulos";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id) {
        service.eliminar(id);
        return "redirect:/articulos";
    }
}
