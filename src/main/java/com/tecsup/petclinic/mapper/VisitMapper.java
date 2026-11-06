package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * Mapper between Visit entity and VisitDTO
 *
 */
@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VisitMapper {

	VisitMapper INSTANCE = Mappers.getMapper(VisitMapper.class);

	@Mapping(source = "visitDate", target = "visitDate", dateFormat = "yyyy-MM-dd")
	@Mapping(source = "pet.id", target = "petId")
	VisitDTO mapToDto(Visit visit);

	List<VisitDTO> mapToDtoList(List<Visit> visitList);

}
