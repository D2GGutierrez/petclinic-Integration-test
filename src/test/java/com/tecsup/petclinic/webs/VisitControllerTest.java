package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Visit REST endpoints
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	/**
	 * GET /visits -> 200 OK and JSON list
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindAllVisits() throws Exception {

		final int ID_FIRST_RECORD = 1;

		this.mockMvc.perform(get("/visits"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
	}

	/**
	 * GET /visits/1 -> 200 OK and description = "rabies shot"
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindVisitOK() throws Exception {

		final int VISIT_ID = 1;
		final String DESCRIPTION = "rabies shot";

		this.mockMvc.perform(get("/visits/" + VISIT_ID))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(VISIT_ID)))
				.andExpect(jsonPath("$.description", is(DESCRIPTION)));
	}

	/**
	 * GET /visits/666 -> 404 Not Found
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindVisitKO() throws Exception {

		final int VISIT_ID_NOT_FOUND = 666;

		this.mockMvc.perform(get("/visits/" + VISIT_ID_NOT_FOUND))
				.andExpect(status().isNotFound());
	}

	/**
	 * POST /visits -> 201 Created
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateVisit() throws Exception {

		String VISIT_DATE = "2025-05-20";
		String DESCRIPTION = "vaccination";
		int PET_ID = 1;

		VisitDTO newVisitTO = VisitDTO.builder()
				.visitDate(VISIT_DATE)
				.description(DESCRIPTION)
				.petId(PET_ID)
				.build();

		ResultActions mvcActions = this.mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newVisitTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.visitDate", is(VISIT_DATE)))
				.andExpect(jsonPath("$.description", is(DESCRIPTION)))
				.andExpect(jsonPath("$.petId", is(PET_ID)));

		// CLEAN UP
		String response = mvcActions.andReturn().getResponse().getContentAsString();
		Integer id = JsonPath.parse(response).read("$.id");

		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());
	}

	/**
	 * POST + PUT /visits/{id} -> 200 OK and change confirmed
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateVisit() throws Exception {

		String VISIT_DATE = "2025-05-20";
		String DESCRIPTION = "vaccination";
		int PET_ID = 1;

		String UP_VISIT_DATE = "2025-06-15";
		String UP_DESCRIPTION = "vaccination booster";
		int UP_PET_ID = 2;

		VisitDTO newVisitTO = VisitDTO.builder()
				.visitDate(VISIT_DATE)
				.description(DESCRIPTION)
				.petId(PET_ID)
				.build();

		// CREATE
		ResultActions mvcActions = mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newVisitTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated());

		String response = mvcActions.andReturn().getResponse().getContentAsString();
		Integer id = JsonPath.parse(response).read("$.id");

		// UPDATE
		VisitDTO upVisitTO = VisitDTO.builder()
				.id(id.longValue())
				.visitDate(UP_VISIT_DATE)
				.description(UP_DESCRIPTION)
				.petId(UP_PET_ID)
				.build();

		mockMvc.perform(put("/visits/" + id)
						.content(om.writeValueAsString(upVisitTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		// FIND
		mockMvc.perform(get("/visits/" + id))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.visitDate", is(UP_VISIT_DATE)))
				.andExpect(jsonPath("$.description", is(UP_DESCRIPTION)))
				.andExpect(jsonPath("$.petId", is(UP_PET_ID)));

		// DELETE
		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());
	}

	/**
	 * POST + DELETE /visits/{id} -> 200 OK
	 *
	 * @throws Exception
	 */
	@Test
	public void testDeleteVisit() throws Exception {

		String VISIT_DATE = "2025-05-20";
		String DESCRIPTION = "vaccination to delete";
		int PET_ID = 1;

		VisitDTO newVisitTO = VisitDTO.builder()
				.visitDate(VISIT_DATE)
				.description(DESCRIPTION)
				.petId(PET_ID)
				.build();

		// CREATE
		ResultActions mvcActions = mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newVisitTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated());

		String response = mvcActions.andReturn().getResponse().getContentAsString();
		Integer id = JsonPath.parse(response).read("$.id");

		// DELETE
		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());

		// FIND -> must not exist anymore
		mockMvc.perform(get("/visits/" + id))
				.andExpect(status().isNotFound());
	}

	/**
	 * GET /visits?petId=7 -> 2 visits of Samantha (visits 1 and 4)
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindByPetId() throws Exception {

		final int PET_ID = 7;   // Samantha
		final int NRO_VISITS = 2;

		this.mockMvc.perform(get("/visits").param("petId", String.valueOf(PET_ID)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_VISITS)))
				.andExpect(jsonPath("$[*].petId", everyItem(is(PET_ID))))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 4)));
	}

	/**
	 * GET /visits?vetId=2 -> visits of vet 2 (Helen Leary: visits 1 and 2)
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindByVetId() throws Exception {

		final int VET_ID = 2;   // Helen Leary
		final int NRO_VISITS = 2;

		this.mockMvc.perform(get("/visits").param("vetId", String.valueOf(VET_ID)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_VISITS)))
				.andExpect(jsonPath("$[*].vetId", everyItem(is(VET_ID))))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)));
	}

	/**
	 * GET /visits/pet/7/total-cost -> 45.00 + 275.00 = 320.00
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalculateTotalCostByPet() throws Exception {

		final int PET_ID = 7;   // Samantha
		final BigDecimal TOTAL_COST = new BigDecimal("320.00");

		String response = this.mockMvc.perform(get("/visits/pet/" + PET_ID + "/total-cost"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		BigDecimal totalCost = new BigDecimal(response);

		assertEquals(0, TOTAL_COST.compareTo(totalCost),
				"Expected total cost " + TOTAL_COST + " but was " + totalCost);
	}

	/**
	 * GET /visits?from=2024-01-01&to=2024-12-31 -> 2 visits (ids 5 and 6)
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindByDateBetween() throws Exception {

		final String FROM = "2024-01-01";
		final String TO = "2024-12-31";
		final int NRO_VISITS = 2;

		this.mockMvc.perform(get("/visits")
						.param("from", FROM)
						.param("to", TO))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_VISITS)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(5, 6)));
	}

	/**
	 * GET /visits?vetId=3&from=2009-01-01&to=2009-12-31 -> only visit 3
	 * (vet 3 also has visit 4 on 2008-09-04, which is outside the range)
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindByVetIdAndDateBetween() throws Exception {

		final int VET_ID = 3;   // Linda Douglas
		final String FROM = "2009-01-01";
		final String TO = "2009-12-31";
		final int NRO_VISITS = 1;
		final int VISIT_ID = 3;

		this.mockMvc.perform(get("/visits")
						.param("vetId", String.valueOf(VET_ID))
						.param("from", FROM)
						.param("to", TO))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_VISITS)))
				.andExpect(jsonPath("$[0].id", is(VISIT_ID)))
				.andExpect(jsonPath("$[0].vetId", is(VET_ID)))
				.andExpect(jsonPath("$[0].visitDate", is("2009-06-04")));
	}

	/**
	 * GET /visits/pet/8/count -> 2 visits of Max (visits 2 and 3)
	 *
	 * @throws Exception
	 */
	@Test
	public void testCountVisitsByPet() throws Exception {

		final int PET_ID = 8;   // Max
		final long NRO_VISITS = 2;

		this.mockMvc.perform(get("/visits/pet/" + PET_ID + "/count"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(NRO_VISITS)));
	}

}
