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
     * @param visitDTO
     * @return
     */
    VisitDTO create(VisitDTO visitDTO);

    /**
     *
     * @param visitDTO
     * @return
     */
    VisitDTO update(VisitDTO visitDTO);

    /**
     *
     * @param id
     * @throws VisitNotFoundException
     */
    void delete(Long id) throws VisitNotFoundException;

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
