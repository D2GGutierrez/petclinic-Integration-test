package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    @Transactional
    public SpecialtyDTO create(SpecialtyDTO specialtyDTO) {
        Specialty newSpecialty = specialtyRepository.save(specialtyMapper.mapToEntity(specialtyDTO));
        return specialtyMapper.mapToDto(newSpecialty);
    }

    @Override
    @Transactional
    public SpecialtyDTO update(SpecialtyDTO specialtyDTO) {
        Specialty updSpecialty = specialtyRepository.save(specialtyMapper.mapToEntity(specialtyDTO));
        return specialtyMapper.mapToDto(updSpecialty);
    }

    @Override
    @Transactional
    public void delete(Integer id) throws SpecialtyNotFoundException {
        findById(id);
        specialtyRepository.deleteById(id);
    }

    @Override
    public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
        return specialtyMapper.mapToDto(specialty);
    }

    @Override
    public List<SpecialtyDTO> findByName(String name) {
        return specialtyMapper.mapToDtoList(specialtyRepository.findByName(name));
    }

    @Override
    public List<SpecialtyDTO> findByOffice(String office) {
        return specialtyMapper.mapToDtoList(specialtyRepository.findByOffice(office));
    }

    @Override
    public List<SpecialtyDTO> findByNameAndOffice(String name, String office) {
        return specialtyMapper.mapToDtoList(specialtyRepository.findByNameAndOffice(name, office));
    }

    @Override
    public List<SpecialtyDTO> findAll() {
        return specialtyMapper.mapToDtoList(specialtyRepository.findAll());
    }

}