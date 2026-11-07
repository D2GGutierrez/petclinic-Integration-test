package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Vet REST endpoints (real stack + H2)
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllVets() throws Exception {

        this.mockMvc.perform(get("/vets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testFindVetOK() throws Exception {

        final int VET_ID = 1;
        final String LAST_NAME = "Carter";

        this.mockMvc.perform(get("/vets/" + VET_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(VET_ID)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)));
    }

    @Test
    public void testFindVetKO() throws Exception {

        final int VET_ID_NOT_FOUND = 666;

        this.mockMvc.perform(get("/vets/" + VET_ID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateVet() throws Exception {

        String FIRST_NAME = "Lucia";
        String LAST_NAME = "Mendoza";
        String EMAIL = "lucia.mendoza@petclinic.com";
        String PHONE = "6085550000";

        VetDTO newVetTO = VetDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();

        ResultActions mvcActions = this.mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.phone", is(PHONE)))
                .andExpect(jsonPath("$.active", is(true)));

        // CLEAN UP
        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeactivateVet() throws Exception {

        VetDTO newVetTO = VetDTO.builder()
                .firstName("Lucia")
                .lastName("Mendoza")
                .email("lucia.deactivate@petclinic.com")
                .phone("6085550001")
                .active(true)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // DEACTIVATE
        mockMvc.perform(put("/vets/" + id + "/deactivate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.active", is(false)));

        // CLEAN UP
        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testReactivateVet() throws Exception {

        final int VET_ID_INACTIVE = 6;   // Sharon Jenkins

        // REACTIVATE
        mockMvc.perform(put("/vets/" + VET_ID_INACTIVE + "/reactivate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(VET_ID_INACTIVE)))
                .andExpect(jsonPath("$.active", is(true)));

        // RESTORE the original state so other tests keep working
        mockMvc.perform(put("/vets/" + VET_ID_INACTIVE + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    public void testFindActiveVets() throws Exception {

        final int NRO_VETS = 5;

        this.mockMvc.perform(get("/vets").param("active", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(NRO_VETS)))
                .andExpect(jsonPath("$[*].active", everyItem(is(true))));
    }

    @Test
    public void testFindInactiveVets() throws Exception {

        final int NRO_VETS = 1;

        this.mockMvc.perform(get("/vets").param("active", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(NRO_VETS)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[*].active", everyItem(is(false))));
    }

    @Test
    public void testDeleteVetKO() throws Exception {

        final int VET_ID_NOT_FOUND = 1000;

        mockMvc.perform(delete("/vets/" + VET_ID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

}