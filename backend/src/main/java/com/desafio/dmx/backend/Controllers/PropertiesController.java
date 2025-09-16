package com.desafio.dmx.backend.Controllers;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Services.PropertiesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/propriedades")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertiesController {

    private final PropertiesService service;

    public PropertiesController(PropertiesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Properties>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Properties> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Properties> create(@Valid @RequestBody Properties property) {
        return ResponseEntity.ok(service.create(property));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Properties> update(@PathVariable Long id, @RequestBody Properties property) {
        return ResponseEntity.ok(service.update(id, property));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
