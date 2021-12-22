package fr.semifir.apicinema.controllers;

import fr.semifir.apicinema.dtos.seance.SeanceDTO;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("seances")
public class SeanceController {

    @Autowired
    SeanceService service;

    @GetMapping
    public List<SeanceDTO> findAll() {
        return this.service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<SeanceDTO> findById(@PathVariable String id) {
        Optional<SeanceDTO> SeanceDTO = null;
        try {
            SeanceDTO = this.service.findByID(id);
            return ResponseEntity.ok(SeanceDTO.get());
        } catch (Exception e) {
           return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<SeanceDTO> save(@RequestBody SeanceDTO seance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(seance));
    }

    @PutMapping
    public ResponseEntity<SeanceDTO> update(@RequestBody SeanceDTO seance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(seance));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody Seance seance) {
        this.service.delete(seance);
        return ResponseEntity.ok(true);
    }
}
