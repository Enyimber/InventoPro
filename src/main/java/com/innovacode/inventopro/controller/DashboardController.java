package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.service.ArticuloService;
import com.innovacode.inventopro.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ArticuloService articuloService;
    private final MovimientoService movimientoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var todos = articuloService.listar();
        var bajos = articuloService.bajoStock();
        var hoy = LocalDate.now();
        long movHoy = movimientoService.listar().stream()
            .filter(m -> m.getFecha() != null && m.getFecha().toLocalDate().equals(hoy))
            .count();
        model.addAttribute("totalArticulos", todos.size());
        model.addAttribute("articulosBajos", bajos);
        model.addAttribute("totalAlertas", bajos.size());
        model.addAttribute("movimientosHoy", movHoy);
        return "dashboard";
    }
}
