package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Specialty REST endpoints (real stack + H2)
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /specialties -> 200 OK and JSON list
     */
    @Test
    public void testFindAllSpecialties() throws Exception {

        this.mockMvc.perform(get("/specialties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * GET /specialties/1 -> 200 OK and name = "radiology"
     */
    @Test
    public void testFindSpecialtyOK() throws Exception {

        final int SPECIALTY_ID = 1;
        final String NAME = "radiology";

        this.mockMvc.perform(get("/specialties/" + SPECIALTY_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(SPECIALTY_ID)))
                .andExpect(jsonPath("$.name", is(NAME)));
    }

    /**
     * GET /specialties/666 -> 404 Not Found
     */
    @Test
    public void testFindSpecialtyKO() throws Exception {

        final int SPECIALTY_ID_NOT_FOUND = 666;

        this.mockMvc.perform(get("/specialties/" + SPECIALTY_ID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /specialties -> 201 Created
     */
    @Test
    public void testCreateSpecialty() throws Exception {

        String NAME = "neurology";
        String OFFICE = "Cusco";
        int OPEN_HOUR = 8;
        int CLOSE_HOUR = 14;

        SpecialtyDTO newSpecialtyTO = SpecialtyDTO.builder()
                .name(NAME)
                .office(OFFICE)
                .openHour(OPEN_HOUR)
                .closeHour(CLOSE_HOUR)
                .build();

        ResultActions mvcActions = this.mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialtyTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.office", is(OFFICE)))
                .andExpect(jsonPath("$.openHour", is(OPEN_HOUR)))
                .andExpect(jsonPath("$.closeHour", is(CLOSE_HOUR)));

        // CLEAN UP
        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());
    }

    /**
     * GET /specialties?name=surgery -> only surgery (id 2)
     */
    @Test
    public void testFindByName() throws Exception {

        final String NAME = "surgery";
        final int NRO_SPECIALTIES = 1;

        this.mockMvc.perform(get("/specialties").param("name", NAME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(NRO_SPECIALTIES)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is(NAME)));
    }

    /**
     * GET /specialties?office=Farewell -> radiology
     */
    @Test
    public void testFindByOffice() throws Exception {

        final String OFFICE = "Farewell";
        final int NRO_SPECIALTIES = 1;

        this.mockMvc.perform(get("/specialties").param("office", OFFICE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(NRO_SPECIALTIES)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")))
                .andExpect(jsonPath("$[0].office", is(OFFICE)));
    }

    /**
     * POST with openHour >= closeHour -> 400 Bad Request
     */
    @Test
    public void testCreateSpecialty_InvalidSchedule() throws Exception {

        // openHour greater than closeHour
        SpecialtyDTO openAfterCloseTO = SpecialtyDTO.builder()
                .name("invalid schedule")
                .office("Cusco")
                .openHour(18)
                .closeHour(8)
                .build();

        this.mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(openAfterCloseTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // openHour equal to closeHour
        SpecialtyDTO openEqualsCloseTO = SpecialtyDTO.builder()
                .name("invalid schedule")
                .office("Cusco")
                .openHour(10)
                .closeHour(10)
                .build();

        this.mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(openEqualsCloseTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * POST with hours outside 0-23 -> 400 Bad Request
     */
    @Test
    public void testCreateSpecialty_InvalidHourRange() throws Exception {

        SpecialtyDTO invalidRangeTO = SpecialtyDTO.builder()
                .name("invalid hour range")
                .office("Cusco")
                .openHour(-1)
                .closeHour(25)
                .build();

        this.mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(invalidRangeTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * POST + DELETE /specialties/{id} -> 200 OK
     */
    @Test
    public void testDeleteSpecialty() throws Exception {

        SpecialtyDTO newSpecialtyTO = SpecialtyDTO.builder()
                .name("specialty to delete")
                .office("Cusco")
                .openHour(9)
                .closeHour(17)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialtyTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // DELETE
        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());

        // FIND -> must not exist anymore
        mockMvc.perform(get("/specialties/" + id))
                .andExpect(status().isNotFound());
    }

}