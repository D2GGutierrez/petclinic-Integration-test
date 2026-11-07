package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import java.util.List;

public interface OwnerService {

    OwnerDTO create(OwnerDTO ownerDTO);

    OwnerDTO update(OwnerDTO ownerDTO);

    void delete(Long id) throws OwnerNotFoundException;

    OwnerDTO findById(Long id) throws OwnerNotFoundException;

    List<OwnerDTO> findByLastName(String lastName);

    List<OwnerDTO> findAll();

}