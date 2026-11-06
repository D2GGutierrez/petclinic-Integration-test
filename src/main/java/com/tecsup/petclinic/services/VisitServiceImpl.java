package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.mapper.VisitMapper;
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
    VisitMapper visitMapper;

    public VisitServiceImpl(VisitRepository visitRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
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
}
