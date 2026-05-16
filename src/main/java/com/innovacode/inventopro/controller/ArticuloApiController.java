package com.innovacode.inventopro.controller;

import com.innovacode.inventopro.model.Articulo;
import com.innovacode.inventopro.service.ArticuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
public class ArticuloApiController {
    private final ArticuloService service;

    @GetMapping public List<Articulo> listar() { return service.listar(); }
    @GetMapping("/{id}") public Articulo obtener(@PathVariable String id) { return service.obtener(id); }
    @PostMapping public ResponseEntity<Articulo> crear(@RequestBody Articulo a) {
        return ResponseEntity.status(201).body(service.crear(a));
    }
    @PutMapping("/{id}") public Articulo actualizar(@PathVariable String id, @RequestBody Articulo a) {
        return service.actualizar(id, a);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
    @GetMapping("/alertas") public List<Articulo> alertas() { return service.bajoStock(); }
    @GetMapping("/buscar")  public List<Articulo> buscar(@RequestParam String q) { return service.buscar(q); }
}
