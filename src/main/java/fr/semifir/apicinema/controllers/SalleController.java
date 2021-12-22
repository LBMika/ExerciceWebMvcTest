package fr.semifir.apicinema.controllers;

import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.services.SalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("salles")
public class SalleController {

    @Autowired
    SalleService service;

    @GetMapping
    public List<SalleDTO> findAll() {
        return this.service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<SalleDTO> findById(@PathVariable String id) {
        Optional<SalleDTO> SalleDTO = null;
        try {
            SalleDTO = this.service.findByID(id);
            return ResponseEntity.ok(SalleDTO.get());
        } catch (Exception e) {
           return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<SalleDTO> save(@RequestBody SalleDTO salle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(salle));
    }

    @PutMapping
    public ResponseEntity<SalleDTO> update(@RequestBody SalleDTO salle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(salle));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody Salle salle) {
        this.service.delete(salle);
        return ResponseEntity.ok(true);
    }
}
