package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 *
 * DTO for Visit
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VisitDTO {

	private Long id;

	private String visitDate;

	private String description;

	private BigDecimal cost;

	private Integer petId;

	private Integer vetId;

}
