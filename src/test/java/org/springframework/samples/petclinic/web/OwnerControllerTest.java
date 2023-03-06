package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    @Autowired
    OwnerController ownerController;

    @Autowired
    ClinicService clinicService;

    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @AfterEach
    void tearDown() {
        reset(clinicService);
    }

    @Test
    void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                    .param("firstName", "Jimmy")
                    .param("lastName", "Buffett")
                    .param("address", "123 Duval St ")
                    .param("city", "Key West")
                    .param("telephone", "123123123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testNewOwnerPostNotValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                        .param("firstName", "Jimmy")
                        .param("lastName", "Buffett")
                        .param("city", "Key West"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessUpdateOwnerFormValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                    .param("firstName", "Jimmy")
                    .param("lastName", "Buffett")
                    .param("address", "123 Duval St ")
                    .param("city", "Key West")
                    .param("telephone", "123123123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessUpdateOwnerFormNotValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                        .param("firstName", "Jimmy")
                        .param("address", "123 Duval St ")
                        .param("telephone", "123123123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
                .andExpect(model().attributeHasFieldErrors("owner", "city"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testFindByNameSingeResult() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        when(clinicService.findOwnerByLastName(anyString())).thenReturn(Lists.list(owner));
        mockMvc.perform(get("/owners")
                    .param("lastName", "LastName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }

    @Test
    void testReturnListOfOwners() throws Exception {
        when(clinicService.findOwnerByLastName("")).thenReturn(Lists.list(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));

        verify(clinicService).findOwnerByLastName(stringArgumentCaptor.capture());
        assertEquals("", stringArgumentCaptor.getValue());
    }

    @Test
    void testFindByNameNotFound() throws Exception {
        mockMvc.perform(get("/owners")
                        .param("lastName", "Dont find me"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));

    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
}