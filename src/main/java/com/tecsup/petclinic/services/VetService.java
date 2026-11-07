package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;

import java.util.List;

public interface VetService {

    VetDTO create(VetDTO vetDTO);

    VetDTO update(VetDTO vetDTO);

    void delete(Integer id) throws VetNotFoundException;

    VetDTO findById(Integer id) throws VetNotFoundException;

    VetDTO deactivate(Integer id) throws VetNotFoundException;

    VetDTO reactivate(Integer id) throws VetNotFoundException;

    List<VetDTO> findByActive(Boolean active);

    List<VetDTO> findAll();

}