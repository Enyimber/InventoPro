package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Sprint 4 · HU-12 — Endpoints de exportación PDF/Excel.
 * Acceso: ADMIN, GERENTE, AUDITOR.
 */
@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','GERENTE','AUDITOR')")
public class ReporteController {

    private final ReporteService rep;

    @GetMapping
    public String index() { return "reportes/index"; }

    // --------- PDF ---------
    @GetMapping("/articulos.pdf")
    public ResponseEntity<byte[]> articulosPdf() {
        return pdf("articulos.pdf", rep.articulosPdf());
    }
    @GetMapping("/bajo-stock.pdf")
    public ResponseEntity<byte[]> bajoStockPdf() {
        return pdf("bajo_stock.pdf", rep.bajoStockPdf());
    }
    @GetMapping("/movimientos.pdf")
    public ResponseEntity<byte[]> movimientosPdf() {
        return pdf("movimientos.pdf", rep.movimientosPdf());
    }

    // --------- EXCEL ---------
    @GetMapping("/articulos.xlsx")
    public ResponseEntity<byte[]> articulosXlsx() {
        return xlsx("articulos.xlsx", rep.articulosXlsx());
    }
    @GetMapping("/bajo-stock.xlsx")
    public ResponseEntity<byte[]> bajoStockXlsx() {
        return xlsx("bajo_stock.xlsx", rep.bajoStockXlsx());
    }
    @GetMapping("/movimientos.xlsx")
    public ResponseEntity<byte[]> movimientosXlsx() {
        return xlsx("movimientos.xlsx", rep.movimientosXlsx());
    }

    private ResponseEntity<byte[]> pdf(String name, byte[] body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
    private ResponseEntity<byte[]> xlsx(String name, byte[] body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(body);
    }
}
