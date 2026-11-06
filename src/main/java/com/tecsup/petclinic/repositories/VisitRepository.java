package com.tecsup.petclinic.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

	@Override
	List<Visit> findAll();

}
