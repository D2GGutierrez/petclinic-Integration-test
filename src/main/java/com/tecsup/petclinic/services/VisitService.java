package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;

import java.util.List;

/**
 *
 * Service for Visit
 *
 */
public interface VisitService {

    /**
     *
     * @param id
     * @return
     * @throws VisitNotFoundException
     */
    VisitDTO findById(Long id) throws VisitNotFoundException;

    /**
     *
     * @return
     */
    List<VisitDTO> findAll();
}
