package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping(value = "/owners")
    public ResponseEntity<List<OwnerDTO>> findAllOwners(@RequestParam(required = false) String lastName) {

        List<OwnerDTO> ownersTO = (lastName != null)
                ? ownerService.findByLastName(lastName)
                : ownerService.findAll();

        return ResponseEntity.ok(ownersTO);
    }

    @PostMapping(value = "/owners")
    ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerTO) {

        if (ownerTO.getLastName() == null || ownerTO.getLastName().isBlank())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(ownerTO));
    }

    @GetMapping(value = "/owners/{id}")
    ResponseEntity<OwnerDTO> findById(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(ownerService.findById(id));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/owners/{id}")
    ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO ownerTO, @PathVariable Long id) {

        try {
            OwnerDTO updateOwnerDto = ownerService.findById(id);

            updateOwnerDto.setFirstName(ownerTO.getFirstName());
            updateOwnerDto.setLastName(ownerTO.getLastName());
            updateOwnerDto.setAddress(ownerTO.getAddress());
            updateOwnerDto.setCity(ownerTO.getCity());
            updateOwnerDto.setTelephone(ownerTO.getTelephone());

            return ResponseEntity.ok(ownerService.update(updateOwnerDto));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/owners/{id}")
    ResponseEntity<String> delete(@PathVariable Long id) {

        try {
            ownerService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}