package com.desafio.dmx.backend.Controllers;

import com.desafio.dmx.backend.Entities.Safra;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Services.SafraService;
import com.desafio.dmx.backend.Services.TalhaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/safra")
@CrossOrigin(origins = "http://localhost:3000")
public class SafraController {

    private final SafraService safraService;
    private final TalhaoService TalhaoService;

    public SafraController(SafraService safraService, TalhaoService talhaoRepository) {
        this.safraService = safraService;
        this.TalhaoService = talhaoRepository;
    }


    @GetMapping
    public ResponseEntity<List<Safra>> getAll() {
        return ResponseEntity.ok(safraService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Safra> getById(@PathVariable Long id) {
        return safraService.getAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Safra> create(@RequestBody Safra safra) {
        if (safra.getTalhao() == null || safra.getTalhao().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Talhao talhao = TalhaoService.findById(safra.getTalhao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Talhão não encontrado"));

        safra.setTalhao(talhao);
        return ResponseEntity.ok(safraService.create(safra));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Safra> update(@PathVariable Long id, @RequestBody Safra safra) {
        if (safra.getTalhao() == null || safra.getTalhao().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Talhao talhao = TalhaoService.findById(safra.getTalhao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Talhão não encontrado"));

        safra.setTalhao(talhao);
        return ResponseEntity.ok(safraService.update(id, safra));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        safraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
