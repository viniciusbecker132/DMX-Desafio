package com.desafio.dmx.backend.Controllers;

import com.desafio.dmx.backend.Entities.Properties;
import com.desafio.dmx.backend.Entities.Talhao;
import com.desafio.dmx.backend.Repositories.PropertiesRepository;
import com.desafio.dmx.backend.Services.PropertiesService;
import com.desafio.dmx.backend.Services.TalhaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/talhao")
@CrossOrigin(origins = "http://localhost:3000")
public class TalhaoController {

    private final TalhaoService service;
    private final PropertiesService propertiesService;

    public TalhaoController(TalhaoService service, PropertiesService propertiesService) {
        this.service = service;
        this.propertiesService = propertiesService;
    }

    // Todos podem acessar GET
    @GetMapping
    public ResponseEntity<List<Talhao>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Talhao> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRUD apenas para ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Talhao create(@RequestBody Talhao talhao) throws Exception {
        if (talhao.getArea() == null || talhao.getArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A área deve ser maior que zero.");
        }

        if (talhao.getPropriedade() == null || talhao.getPropriedade().getId() == null) {
            throw new IllegalArgumentException("É necessário informar uma propriedade válida.");
        }

        Properties propriedade = propertiesService.findById(talhao.getPropriedade().getId())
                .orElseThrow(() -> new IllegalArgumentException("Propriedade não encontrada: " + talhao.getPropriedade().getId()));

        if (talhao.getArea().compareTo(propriedade.getArea()) > 0) {
            throw new Exception("Área do talhão não pode ser maior que da propriedade");
        }
        talhao.setPropriedade(propriedade);

        return service.create(talhao);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Talhao> update(@PathVariable Long id, @RequestBody Talhao talhao) {
        return ResponseEntity.ok(service.update(id, talhao));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<String> importCSV(@RequestParam("file") MultipartFile file) {
        try {
            service.importCSV(file);
            return ResponseEntity.ok("CSV importado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao importar CSV: " + e.getMessage());
        }
    }
}

