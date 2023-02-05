package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    @Mock
    ClinicService clinicService;

    @Mock
    Map<String, Object> model;

    @InjectMocks
    VetController vetController;

    List<Vet> vetList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Vet vet = new Vet();
        vetList.add(vet);
        when(clinicService.findVets()).thenReturn(vetList);
    }

    @Test
    void showVetList() {
        //when
        String response = vetController.showVetList(model);
        //then
        verify(clinicService).findVets();
        verify(model).put(anyString(), any());
        assertNotNull(response);
        assertEquals("vets/vetList", response);
    }

    @Test
    void showResourcesVetList() {
        //when
        Vets vets = vetController.showResourcesVetList();
        //then
        assertNotNull(vets);
        verify(clinicService).findVets();
        assertNotEquals(0, vets.getVetList().size());
    }
}