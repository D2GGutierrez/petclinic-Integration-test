package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.mapper.VisitMapper;
import com.tecsup.petclinic.repositories.PetRepository;
import com.tecsup.petclinic.repositories.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 *
 * Service implementation for Visit
 *
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class VisitServiceImpl implements VisitService {

    VisitRepository visitRepository;
    PetRepository petRepository;
    VisitMapper visitMapper;

    public VisitServiceImpl(VisitRepository visitRepository, PetRepository petRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.petRepository = petRepository;
        this.visitMapper = visitMapper;
    }

    /**
     *
     * @param visitDTO
     * @return
     */
    @Override
    @Transactional
    public VisitDTO create(VisitDTO visitDTO) {

        Visit newVisit = visitRepository.save(toEntity(visitDTO));

        return visitMapper.mapToDto(newVisit);
    }

    /**
     *
     * @param visitDTO
     * @return
     */
    @Override
    @Transactional
    public VisitDTO update(VisitDTO visitDTO) {

        Visit updVisit = visitRepository.save(toEntity(visitDTO));

        return visitMapper.mapToDto(updVisit);
    }

    /**
     *
     * @param id
     * @throws VisitNotFoundException
     */
    @Override
    @Transactional
    public void delete(Long id) throws VisitNotFoundException {

        findById(id);

        visitRepository.deleteById(id);
    }

    /**
     *
     * @param id
     * @return
     * @throws VisitNotFoundException
     */
    @Override
    public VisitDTO findById(Long id) throws VisitNotFoundException {

        Optional<Visit> visit = visitRepository.findById(id);

        if (!visit.isPresent())
            throw new VisitNotFoundException("Record not found...!");

        return this.visitMapper.mapToDto(visit.get());
    }

    /**
     *
     * @return
     */
    @Override
    public List<VisitDTO> findAll() {

        List<Visit> visits = visitRepository.findAll();

        return this.visitMapper.mapToDtoList(visits);
    }

    /**
     * Map DTO to entity and resolve the pet relation from petId
     *
     * @param visitDTO
     * @return
     */
    private Visit toEntity(VisitDTO visitDTO) {

        Visit visit = visitMapper.mapToEntity(visitDTO);

        if (visitDTO.getPetId() != null)
            visit.setPet(petRepository.getReferenceById(visitDTO.getPetId()));

        return visit;
    }
}
