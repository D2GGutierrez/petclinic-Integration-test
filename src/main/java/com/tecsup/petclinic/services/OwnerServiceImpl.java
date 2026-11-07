package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.repositories.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    private final OwnerMapper ownerMapper;

    public OwnerServiceImpl(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    @Override
    @Transactional
    public OwnerDTO create(OwnerDTO ownerDTO) {
        Owner newOwner = ownerRepository.save(ownerMapper.mapToEntity(ownerDTO));
        return ownerMapper.mapToDto(newOwner);
    }

    @Override
    @Transactional
    public OwnerDTO update(OwnerDTO ownerDTO) {
        Owner updOwner = ownerRepository.save(ownerMapper.mapToEntity(ownerDTO));
        return ownerMapper.mapToDto(updOwner);
    }

    @Override
    @Transactional
    public void delete(Long id) throws OwnerNotFoundException {
        findById(id);
        ownerRepository.deleteById(id);
    }

    @Override
    public OwnerDTO findById(Long id) throws OwnerNotFoundException {
        Optional<Owner> owner = ownerRepository.findById(id);

        if (!owner.isPresent())
            throw new OwnerNotFoundException("Record not found...!");

        return ownerMapper.mapToDto(owner.get());
    }

    @Override
    public List<OwnerDTO> findByLastName(String lastName) {
        return ownerMapper.mapToDtoList(ownerRepository.findByLastName(lastName));
    }

    @Override
    public List<OwnerDTO> findAll() {
        return ownerMapper.mapToDtoList(ownerRepository.findAll());
    }

}