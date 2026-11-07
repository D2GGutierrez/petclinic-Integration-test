package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional
    public VetDTO create(VetDTO vetDTO) {
        Vet vet = vetMapper.mapToEntity(vetDTO);
        if (vet.getActive() == null)
            vet.setActive(true);
        return vetMapper.mapToDto(vetRepository.save(vet));
    }

    @Override
    @Transactional
    public VetDTO update(VetDTO vetDTO) {
        return vetMapper.mapToDto(vetRepository.save(vetMapper.mapToEntity(vetDTO)));
    }

    @Override
    @Transactional
    public void delete(Integer id) throws VetNotFoundException {
        findById(id);
        vetRepository.deleteById(id);
    }

    @Override
    public VetDTO findById(Integer id) throws VetNotFoundException {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Record not found...!"));
        return vetMapper.mapToDto(vet);
    }

    @Override
    @Transactional
    public VetDTO deactivate(Integer id) throws VetNotFoundException {
        return changeActive(id, false);
    }

    @Override
    @Transactional
    public VetDTO reactivate(Integer id) throws VetNotFoundException {
        return changeActive(id, true);
    }

    @Override
    public List<VetDTO> findByActive(Boolean active) {
        return vetMapper.mapToDtoList(vetRepository.findByActive(active));
    }

    @Override
    public List<VetDTO> findAll() {
        return vetMapper.mapToDtoList(vetRepository.findAll());
    }

    private VetDTO changeActive(Integer id, boolean active) throws VetNotFoundException {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Record not found...!"));
        vet.setActive(active);
        return vetMapper.mapToDto(vetRepository.save(vet));
    }

}