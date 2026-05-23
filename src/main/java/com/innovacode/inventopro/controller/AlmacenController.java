package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/almacen")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ALMACENISTA','SUPERVISOR')")
public class AlmacenController {

    private final MovimientoService movService;

    @PostMapping("/entregas/aceptar/{id}")
    public String aceptar(@PathVariable String id, Authentication auth, RedirectAttributes ra) {
        try {
            movService.procesarEntrega(id, true, auth.getName());
            ra.addFlashAttribute("ok", "Entrega aceptada y stock actualizado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/entregas/rechazar/{id}")
    public String rechazar(@PathVariable String id, Authentication auth, RedirectAttributes ra) {
        try {
            movService.procesarEntrega(id, false, auth.getName());
            ra.addFlashAttribute("ok", "Entrega rechazada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
