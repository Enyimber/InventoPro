package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.model.Movimiento;
import com.innovacode.inventopro.service.ArticuloService;
import com.innovacode.inventopro.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoWebController {

    private final MovimientoService service;
    private final ArticuloService articuloService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("movimientos", service.listar());
        Map<String, String> articulosMap = articuloService.listar().stream()
            .collect(Collectors.toMap(Articulo::getId, Articulo::getNombre, (o1, o2) -> o1));
        model.addAttribute("articulosMap", articulosMap);
        return "movimientos/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ALMACENISTA','VENDEDOR','COMPRADOR')")
    public String nuevo(@RequestParam(required = false, defaultValue = "SALIDA") String tipo,
                        Model model) {
        Movimiento m = new Movimiento();
        m.setTipo(tipo.toUpperCase());
        model.addAttribute("movimiento", m);
        model.addAttribute("articulos", articuloService.listar());
        return "movimientos/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ALMACENISTA','VENDEDOR','COMPRADOR')")
    public String crear(@ModelAttribute Movimiento movimiento,
                        Authentication auth, RedirectAttributes ra) {
        try {
            service.registrar(movimiento, auth.getName());
            ra.addFlashAttribute("ok",
                ("ENTRADA".equals(movimiento.getTipo()) ? "Entrada" : "Salida")
                + " registrada y stock actualizado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientos";
    }
}
