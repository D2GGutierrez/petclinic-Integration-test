package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping(value = "/vets")
    public ResponseEntity<List<VetDTO>> findAllVets(@RequestParam(required = false) Boolean active) {

        List<VetDTO> vetsTO = (active != null)
                ? vetService.findByActive(active)
                : vetService.findAll();

        return ResponseEntity.ok(vetsTO);
    }

    @PostMapping(value = "/vets")
    ResponseEntity<VetDTO> create(@RequestBody VetDTO vetTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vetService.create(vetTO));
    }

    @GetMapping(value = "/vets/{id}")
    ResponseEntity<VetDTO> findById(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(vetService.findById(id));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/vets/{id}/deactivate")
    ResponseEntity<VetDTO> deactivate(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(vetService.deactivate(id));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/vets/{id}/reactivate")
    ResponseEntity<VetDTO> reactivate(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(vetService.reactivate(id));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/vets/{id}")
    ResponseEntity<String> delete(@PathVariable Integer id) {

        try {
            vetService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}