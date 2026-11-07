package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;

import java.util.List;

public interface SpecialtyService {

    SpecialtyDTO create(SpecialtyDTO specialtyDTO);

    SpecialtyDTO update(SpecialtyDTO specialtyDTO);

    void delete(Integer id) throws SpecialtyNotFoundException;

    SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException;

    List<SpecialtyDTO> findByName(String name);

    List<SpecialtyDTO> findByOffice(String office);

    List<SpecialtyDTO> findByNameAndOffice(String name, String office);

    List<SpecialtyDTO> findAll();

}