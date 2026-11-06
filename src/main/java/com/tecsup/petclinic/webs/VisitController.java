package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * REST controller for Visit
 *
 */
@RestController
@Slf4j
public class VisitController {

	private VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	/**
	 * Get all visits
	 *
	 * @return
	 */
	@GetMapping(value = "/visits")
	public ResponseEntity<List<VisitDTO>> findAllVisits() {

		List<VisitDTO> visitsTO = visitService.findAll();
		visitsTO.forEach(item -> log.info("VisitTO >>  {} ", item));

		return ResponseEntity.ok(visitsTO);
	}

	/**
	 * Find visit by id
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/visits/{id}")
	ResponseEntity<VisitDTO> findById(@PathVariable Long id) {

		VisitDTO visitDto = null;

		try {
			visitDto = visitService.findById(id);

		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(visitDto);
	}

}
