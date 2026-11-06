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

	/**
	 * The pet and vet relations are resolved in the service from petId / vetId
	 */
	@Mapping(source = "visitDate", target = "visitDate", dateFormat = "yyyy-MM-dd")
	@Mapping(target = "pet", ignore = true)
	@Mapping(target = "vet", ignore = true)
	Visit mapToEntity(VisitDTO visitTO);

	@Mapping(source = "visitDate", target = "visitDate", dateFormat = "yyyy-MM-dd")
	@Mapping(source = "pet.id", target = "petId")
	@Mapping(source = "vet.id", target = "vetId")
	VisitDTO mapToDto(Visit visit);

	List<VisitDTO> mapToDtoList(List<Visit> visitList);

}
