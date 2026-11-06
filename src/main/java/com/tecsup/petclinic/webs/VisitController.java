package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
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
	 * Get all visits, optionally filtered by:
	 *  - petId and/or vetId
	 *  - from + to (date range, both inclusive), alone or with vetId
	 *
	 * @param petId
	 * @param vetId
	 * @param from
	 * @param to
	 * @return 400 Bad Request if only one of from / to is sent, or if the range is combined with petId
	 */
	@GetMapping(value = "/visits")
	public ResponseEntity<List<VisitDTO>> findAllVisits(
			@RequestParam(required = false) Integer petId,
			@RequestParam(required = false) Integer vetId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

		List<VisitDTO> visitsTO;

		boolean hasRange = from != null || to != null;

		if (hasRange && (from == null || to == null || petId != null))
			return ResponseEntity.badRequest().build();

		if (hasRange && vetId != null)
			visitsTO = visitService.findByVetIdAndDateBetween(vetId, from, to);
		else if (hasRange)
			visitsTO = visitService.findByDateBetween(from, to);
		else if (petId != null && vetId != null)
			visitsTO = visitService.findByPetIdAndVetId(petId, vetId);
		else if (petId != null)
			visitsTO = visitService.findByPetId(petId);
		else if (vetId != null)
			visitsTO = visitService.findByVetId(vetId);
		else
			visitsTO = visitService.findAll();

		visitsTO.forEach(item -> log.info("VisitTO >>  {} ", item));

		return ResponseEntity.ok(visitsTO);
	}

	/**
	 * Number of visits of a pet
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/visits/pet/{id}/count")
	ResponseEntity<Long> countVisitsByPet(@PathVariable Integer id) {

		long count = visitService.countVisitsByPet(id);

		return ResponseEntity.ok(count);
	}

	/**
	 * Total cost of all visits of a pet
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/visits/pet/{id}/total-cost")
	ResponseEntity<BigDecimal> calculateTotalCostByPet(@PathVariable Integer id) {

		BigDecimal totalCost = visitService.calculateTotalCostByPet(id);

		return ResponseEntity.ok(totalCost);
	}

	/**
	 * Create visit
	 *
	 * @param visitTO
	 * @return
	 */
	@PostMapping(value = "/visits")
	ResponseEntity<VisitDTO> create(@RequestBody VisitDTO visitTO) {

		VisitDTO newVisitTO = visitService.create(visitTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(newVisitTO);
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

	/**
	 * Update visit
	 *
	 * @param visitTO
	 * @param id
	 * @return
	 */
	@PutMapping(value = "/visits/{id}")
	ResponseEntity<VisitDTO> update(@RequestBody VisitDTO visitTO, @PathVariable Long id) {

		VisitDTO updateVisitDto = null;

		try {

			updateVisitDto = visitService.findById(id);

			updateVisitDto.setVisitDate(visitTO.getVisitDate());
			updateVisitDto.setDescription(visitTO.getDescription());
			updateVisitDto.setCost(visitTO.getCost());
			updateVisitDto.setPetId(visitTO.getPetId());
			updateVisitDto.setVetId(visitTO.getVetId());

			updateVisitDto = visitService.update(updateVisitDto);

		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(updateVisitDto);
	}

	/**
	 * Delete visit by id
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/visits/{id}")
	ResponseEntity<String> delete(@PathVariable Long id) {

		try {
			visitService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
