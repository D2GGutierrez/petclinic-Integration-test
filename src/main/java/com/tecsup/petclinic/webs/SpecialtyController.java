package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SpecialtyController {

    private static final int MIN_HOUR = 0;

    private static final int MAX_HOUR = 23;

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping(value = "/specialties")
    public ResponseEntity<List<SpecialtyDTO>> findAllSpecialties(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String office) {

        List<SpecialtyDTO> specialtiesTO;

        if (name != null && office != null)
            specialtiesTO = specialtyService.findByNameAndOffice(name, office);
        else if (name != null)
            specialtiesTO = specialtyService.findByName(name);
        else if (office != null)
            specialtiesTO = specialtyService.findByOffice(office);
        else
            specialtiesTO = specialtyService.findAll();

        return ResponseEntity.ok(specialtiesTO);
    }

    @PostMapping(value = "/specialties")
    ResponseEntity<SpecialtyDTO> create(@RequestBody SpecialtyDTO specialtyTO) {

        if (!isValid(specialtyTO))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(specialtyTO));
    }

    @GetMapping(value = "/specialties/{id}")
    ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {

        try {
            return ResponseEntity.ok(specialtyService.findById(id));
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/specialties/{id}")
    ResponseEntity<String> delete(@PathVariable Integer id) {

        try {
            specialtyService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * name is required, hours must be within 0-23 and openHour must be before closeHour
     */
    private boolean isValid(SpecialtyDTO specialtyTO) {

        if (specialtyTO.getName() == null || specialtyTO.getName().isBlank())
            return false;

        Integer open = specialtyTO.getOpenHour();
        Integer close = specialtyTO.getCloseHour();

        if (open == null || close == null)
            return false;

        if (open < MIN_HOUR || open > MAX_HOUR || close < MIN_HOUR || close > MAX_HOUR)
            return false;

        return open < close;
    }

}