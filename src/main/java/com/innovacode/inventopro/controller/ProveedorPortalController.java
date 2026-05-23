package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.model.Movimiento;
import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.repository.MovimientoRepository;
import com.innovacode.inventopro.repository.UsuarioRepository;
import com.innovacode.inventopro.service.ArticuloService;
import com.innovacode.inventopro.service.MovimientoService;
import com.innovacode.inventopro.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Portal del PROVEEDOR: ve y registra sus propias entregas (ENTRADAS de stock).
 */
@Controller
@RequestMapping("/proveedor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','PROVEEDOR')")
public class ProveedorPortalController {

    private final MovimientoRepository movRepo;
    private final MovimientoService movService;
    private final ArticuloService articuloService;
    private final ProveedorService proveedorService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping("/entregas")
    public String entregas(Authentication auth, Model model) {
        String pid = proveedorIdDe(auth);
        List<Movimiento> entregas = pid == null ? List.of()
            : movRepo.findByProveedorIdOrderByFechaDesc(pid);
        model.addAttribute("entregas", entregas);
        model.addAttribute("proveedor", pid == null ? null : proveedorService.obtener(pid));

        List<Articulo> artsList = articuloService.listar();
        Map<String, String> articulosMap = artsList.stream()
            .collect(Collectors.toMap(Articulo::getId, Articulo::getNombre, (o1, o2) -> o1));
        model.addAttribute("articulosMap", articulosMap);

        return "proveedor/entregas";
    }

    @GetMapping("/entregas/nueva")
    public String nueva(Authentication auth, Model model) {
        Movimiento m = new Movimiento();
        m.setTipo("ENTRADA");
        m.setProveedorId(proveedorIdDe(auth));
        model.addAttribute("movimiento", m);
        model.addAttribute("articulos", articuloService.listar());
        return "proveedor/form";
    }

    @PostMapping("/entregas")
    public String crear(@ModelAttribute Movimiento movimiento,
                        Authentication auth, RedirectAttributes ra) {
        try {
            movimiento.setTipo("ENTRADA");
            movimiento.setProveedorId(proveedorIdDe(auth));
            movService.registrarPendiente(movimiento, auth.getName());
            ra.addFlashAttribute("ok", "Entrega registrada. En espera de aprobación por parte de un Almacenista.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedor/entregas";
    }

    private String proveedorIdDe(Authentication auth) {
        Usuario u = usuarioRepo.findByUsername(auth.getName()).orElse(null);
        return u == null ? null : u.getProveedorId();
    }
}
