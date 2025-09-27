package com.uteq.sgaac.controller;

import com.uteq.sgaac.dto.RequisitoDTO;
import com.uteq.sgaac.services.RequisitoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requisitos")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class RequisitoApiController {

    private final RequisitoService requisitoService;

    public RequisitoApiController(RequisitoService requisitoService) {
        this.requisitoService = requisitoService;
    }

    @GetMapping
    public List<RequisitoDTO> getAll() {
        return requisitoService.findAll();
    }

    @PostMapping
    public RequisitoDTO create(@RequestBody RequisitoDTO dto) {
        dto.setId(null); // Ensure it's a new entity
        return requisitoService.save(dto);
    }

    @PutMapping("/{id}")
    public RequisitoDTO update(@PathVariable Long id, @RequestBody RequisitoDTO dto) {
        return requisitoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requisitoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
