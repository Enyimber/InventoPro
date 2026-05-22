package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.model.Movimiento;
import com.innovacode.inventopro.model.Proveedor;
import com.innovacode.inventopro.model.Usuario;
import com.innovacode.inventopro.repository.MovimientoRepository;
import com.innovacode.inventopro.repository.UsuarioRepository;
import com.innovacode.inventopro.service.ArticuloService;
import com.innovacode.inventopro.service.MovimientoService;
import com.innovacode.inventopro.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dispatcher de dashboards: cada rol ve su propio panel.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ArticuloService articuloService;
    private final MovimientoService movimientoService;
    private final MovimientoRepository movRepo;
    private final ProveedorService proveedorService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // datos comunes
        List<Articulo> articulos = articuloService.listar();
        List<Articulo> bajos = articuloService.bajoStock();
        List<Movimiento> movs = movimientoService.listar();
        
        // Filtrar solo los movimientos procesados/aceptados para las estadísticas y gráficos
        List<Movimiento> movsProcesados = movs.stream()
                .filter(m -> m.getEstado() == null || m.getEstado().equals("ACEPTADO") || m.getEstado().equals("PROCESADO"))
                .collect(Collectors.toList());

        LocalDate hoy = LocalDate.now();
        long movHoy = movsProcesados.stream()
                .filter(m -> m.getFecha() != null && m.getFecha().toLocalDate().equals(hoy))
                .count();

        model.addAttribute("totalArticulos", articulos.size());
        model.addAttribute("totalAlertas", bajos.size());
        model.addAttribute("articulosBajos", bajos);
        model.addAttribute("movimientosHoy", movHoy);
        model.addAttribute("rolPrincipal", rolPrincipal(roles));

        // Entregas pendientes de proveedores
        List<Movimiento> pendientes = movRepo.findByEstadoOrderByFechaDesc("PENDIENTE");
        model.addAttribute("pendientes", pendientes);

        // Mapas auxiliares para nombres legibles
        Map<String, String> articulosMap = articulos.stream()
                .collect(Collectors.toMap(Articulo::getId, Articulo::getNombre, (o1, o2) -> o1));
        model.addAttribute("articulosMap", articulosMap);

        List<Proveedor> provsList = proveedorService.listar();
        Map<String, String> proveedoresMap = provsList.stream()
                .collect(Collectors.toMap(Proveedor::getId, Proveedor::getRazonSocial, (o1, o2) -> o1));
        model.addAttribute("proveedoresMap", proveedoresMap);

        // series últimos 7 días (para gráficas) basado en movimientos procesados
        addSeriesUltimos7Dias(model, movsProcesados);

        // ranking artículos basado en movimientos procesados
        addTopArticulos(model, movsProcesados, articulos);

        // ADMIN tiene jerarquía total -> dashboard admin completo
        if (roles.contains("ROLE_ADMIN")) {
            model.addAttribute("totalUsuarios", usuarioRepo.count());
            model.addAttribute("totalProveedores", proveedorService.listar().size());
            return "dashboards/admin";
        }
        if (roles.contains("ROLE_GERENTE"))     return "dashboards/gerente";
        if (roles.contains("ROLE_AUDITOR"))     return "dashboards/auditor";
        if (roles.contains("ROLE_SUPERVISOR"))  return "dashboards/supervisor";
        if (roles.contains("ROLE_ALMACENISTA")) return "dashboards/almacenista";
        if (roles.contains("ROLE_COMPRADOR"))   return "dashboards/comprador";
        if (roles.contains("ROLE_PROVEEDOR")) {
            // datos propios
            Usuario u = usuarioRepo.findByUsername(auth.getName()).orElse(null);
            String pid = u == null ? null : u.getProveedorId();
            List<Movimiento> propios = pid == null ? List.of()
                : movRepo.findByProveedorIdOrderByFechaDesc(pid);
            model.addAttribute("entregas", propios);
            model.addAttribute("totalEntregas", propios.size());
            model.addAttribute("totalUnidades",
                propios.stream().mapToInt(Movimiento::getCantidad).sum());
            return "dashboards/proveedor";
        }
        return "dashboards/auditor"; // fallback: solo lectura
    }

    private String rolPrincipal(Set<String> roles) {
        String[] orden = {"ROLE_ADMIN","ROLE_GERENTE","ROLE_AUDITOR","ROLE_SUPERVISOR",
                          "ROLE_ALMACENISTA","ROLE_COMPRADOR","ROLE_PROVEEDOR"};
        for (String r : orden) if (roles.contains(r)) return r.substring(5);
        return "USUARIO";
    }

    private void addSeriesUltimos7Dias(Model model, List<Movimiento> movs) {
        List<String> labels = new ArrayList<>();
        List<Long> entradas = new ArrayList<>();
        List<Long> salidas = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            labels.add(d.toString().substring(5)); // MM-dd
            entradas.add(movs.stream().filter(m -> m.getFecha()!=null
                    && m.getFecha().toLocalDate().equals(d)
                    && "ENTRADA".equals(m.getTipo())).count());
            salidas.add(movs.stream().filter(m -> m.getFecha()!=null
                    && m.getFecha().toLocalDate().equals(d)
                    && "SALIDA".equals(m.getTipo())).count());
        }
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartEntradas", entradas);
        model.addAttribute("chartSalidas", salidas);
    }

    private void addTopArticulos(Model model, List<Movimiento> movs, List<Articulo> arts) {
        Map<String,Integer> totales = new HashMap<>();
        for (Movimiento m : movs) {
            totales.merge(m.getArticuloId(), m.getCantidad(), Integer::sum);
        }
        Map<String,String> nombres = new HashMap<>();
        for (Articulo a : arts) nombres.put(a.getId(), a.getNombre());

        List<String> topLabels = new ArrayList<>();
        List<Integer> topValues = new ArrayList<>();
        totales.entrySet().stream()
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> {
                    topLabels.add(nombres.getOrDefault(e.getKey(), e.getKey()));
                    topValues.add(e.getValue());
                });
        model.addAttribute("topLabels", topLabels);
        model.addAttribute("topValues", topValues);
    }
}
