package com.tecsup.petclinic.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tecsup.petclinic.entities.Visit;

/**
 *
 * Repository for Visit
 *
 */
@Repository
public interface VisitRepository
	extends JpaRepository<Visit, Long> {

	// Fetch visits by petId
	List<Visit> findByPetId(Integer petId);

	// Fetch visits by vetId
	List<Visit> findByVetId(Integer vetId);

	// Fetch visits by petId and vetId
	List<Visit> findByPetIdAndVetId(Integer petId, Integer vetId);

	// Fetch visits between two dates (both inclusive)
	List<Visit> findByVisitDateBetween(LocalDate from, LocalDate to);

	// Fetch visits of a vet between two dates (both inclusive)
	List<Visit> findByVetIdAndVisitDateBetween(Integer vetId, LocalDate from, LocalDate to);

	// Count visits of a pet
	long countByPetId(Integer petId);

	// Sum of the cost of all visits of a pet (0 if it has none)
	@Query("SELECT COALESCE(SUM(v.cost), 0) FROM visits v WHERE v.pet.id = :petId")
	BigDecimal sumCostByPetId(@Param("petId") Integer petId);

	@Override
	List<Visit> findAll();

}
