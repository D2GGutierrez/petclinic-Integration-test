package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
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
     * @param petId
     * @return
     */
    List<VisitDTO> findByPetId(Integer petId);

    /**
     *
     * @param vetId
     * @return
     */
    List<VisitDTO> findByVetId(Integer vetId);

    /**
     *
     * @param petId
     * @param vetId
     * @return
     */
    List<VisitDTO> findByPetIdAndVetId(Integer petId, Integer vetId);

    /**
     *
     * @param from
     * @param to
     * @return
     */
    List<VisitDTO> findByDateBetween(LocalDate from, LocalDate to);

    /**
     *
     * @param vetId
     * @param from
     * @param to
     * @return
     */
    List<VisitDTO> findByVetIdAndDateBetween(Integer vetId, LocalDate from, LocalDate to);

    /**
     *
     * @param petId
     * @return
     */
    long countVisitsByPet(Integer petId);

    /**
     *
     * @param petId
     * @return
     */
    BigDecimal calculateTotalCostByPet(Integer petId);

    /**
     *
     * @return
     */
    List<VisitDTO> findAll();
}
