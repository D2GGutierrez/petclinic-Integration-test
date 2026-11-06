package com.tecsup.petclinic.webs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Visit REST endpoints
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerTest {

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

}
