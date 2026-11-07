package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
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
 * Integration tests for the Owner REST endpoints (real stack + H2)
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllOwners() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/owners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    @Test
    public void testFindOwnerOK() throws Exception {

        final int OWNER_ID = 1;
        final String LAST_NAME = "Franklin";

        this.mockMvc.perform(get("/owners/" + OWNER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(OWNER_ID)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)));
    }

    @Test
    public void testFindOwnerKO() throws Exception {

        final int OWNER_ID_NOT_FOUND = 666;

        this.mockMvc.perform(get("/owners/" + OWNER_ID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOwner() throws Exception {

        String FIRST_NAME = "Ana";
        String LAST_NAME = "Torres";
        String ADDRESS = "Av. Lima 123";
        String CITY = "Lima";
        String TELEPHONE = "999888777";

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .address(ADDRESS)
                .city(CITY)
                .telephone(TELEPHONE)
                .build();

        ResultActions mvcActions = this.mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.address", is(ADDRESS)))
                .andExpect(jsonPath("$.city", is(CITY)))
                .andExpect(jsonPath("$.telephone", is(TELEPHONE)));

        // CLEAN UP
        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateOwner_BadRequest() throws Exception {

        // Invalid body: no lastName
        OwnerDTO invalidOwnerTO = OwnerDTO.builder()
                .firstName("Ana")
                .address("Av. Lima 123")
                .city("Lima")
                .telephone("999888777")
                .build();

        this.mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(invalidOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindByLastName() throws Exception {

        final String LAST_NAME = "Davis";
        final int NRO_OWNERS = 2;

        this.mockMvc.perform(get("/owners").param("lastName", LAST_NAME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(NRO_OWNERS)))
                .andExpect(jsonPath("$[0].lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$[1].lastName", is(LAST_NAME)));
    }

    @Test
    public void testUpdateOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName("Ana")
                .lastName("Torres")
                .address("Av. Lima 123")
                .city("Lima")
                .telephone("999888777")
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        String UP_FIRST_NAME = "Ana Maria";
        String UP_LAST_NAME = "Torres Diaz";
        String UP_ADDRESS = "Jr. Cusco 456";
        String UP_CITY = "Arequipa";
        String UP_TELEPHONE = "911222333";

        OwnerDTO upOwnerTO = OwnerDTO.builder()
                .id(id.longValue())
                .firstName(UP_FIRST_NAME)
                .lastName(UP_LAST_NAME)
                .address(UP_ADDRESS)
                .city(UP_CITY)
                .telephone(UP_TELEPHONE)
                .build();

        mockMvc.perform(put("/owners/" + id)
                        .content(om.writeValueAsString(upOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND
        mockMvc.perform(get("/owners/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.firstName", is(UP_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(UP_LAST_NAME)))
                .andExpect(jsonPath("$.address", is(UP_ADDRESS)))
                .andExpect(jsonPath("$.city", is(UP_CITY)))
                .andExpect(jsonPath("$.telephone", is(UP_TELEPHONE)));

        // DELETE
        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName("Ana")
                .lastName("Torres to delete")
                .address("Av. Lima 123")
                .city("Lima")
                .telephone("999888777")
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // DELETE
        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());

        // FIND -> must not exist anymore
        mockMvc.perform(get("/owners/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {

        final int OWNER_ID_NOT_FOUND = 1000;

        mockMvc.perform(delete("/owners/" + OWNER_ID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

}