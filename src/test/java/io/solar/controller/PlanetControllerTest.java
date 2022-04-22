package io.solar.controller;

import io.solar.dto.PlanetDto;
import io.solar.entity.Planet;
import io.solar.mapper.PlanetMapper;
import io.solar.service.PlanetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import io.solar.utils.TestResponsePage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@JsonTest
public class PlanetControllerTest {

    @Autowired
    private JacksonTester<PlanetDto> json;
    @Autowired
    private JacksonTester<TestResponsePage<PlanetDto>> pageJson;

    private MockMvc mockMvc;

    @Mock
    private PlanetService planetService;

    @Spy
    private PlanetMapper planetMapper;

//    @BeforeEach
//    private void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(new PlanetController(planetService, planetMapper))
//                                        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//                                        .build();
//    }

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PLANET"})
    void findById_shouldReturnPlanetByCurrentId() throws Exception {
        long planetId = 500;
        Planet mockPlanet = new Planet();
        mockPlanet.setId(planetId);
        when(planetService.getById(planetId)).thenReturn(mockPlanet);

        MvcResult mvcResult = mockMvc.perform(get("/api/planet/{id}", planetId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        PlanetDto dto = json.parseObject(mvcResult.getResponse().getContentAsString());

        assertEquals(planetId, dto.getId());
    }

    @Test
    void save_shouldWorkCorrectly() throws Exception {
        String planetTitle = "Zhelezyaka";
        PlanetDto requestDto = PlanetDto.builder()
                .title(planetTitle)
                .build();
        Planet saved = new Planet();
        saved.setId(666L);
        saved.setTitle(planetTitle);

        when(planetService.save(any(Planet.class))).thenReturn(saved);
        InOrder inOrder = Mockito.inOrder(planetService, planetMapper);

        MvcResult mvcResult = mockMvc.perform(post("/api/planet")
                                                     .content(json.write(requestDto).getJson())
                                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        inOrder.verify(planetMapper).toEntity(requestDto);
        inOrder.verify(planetService).save(any(Planet.class));
        inOrder.verify(planetMapper).toDto(saved);
        inOrder.verifyNoMoreInteractions();

        PlanetDto dto = json.parseObject(mvcResult.getResponse().getContentAsString());

        assertTrue(dto.getId().equals(saved.getId()) && dto.getTitle().equals(saved.getTitle()));
    }

    @Test
    void findAll_shouldWorkCorrectly() throws Exception {
        String title = "Zhelezyaka";
        Planet planet = new Planet();
        planet.setId(666L);
        planet.setTitle(title);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Planet> page = new PageImpl<>(List.of(planet), pageable, 1);
        when(planetService.findAll(pageable)).thenReturn(page);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "10");

        MvcResult mvcResult = mockMvc
                .perform(get("/api/planet")
                        .params(queryParams))
                .andExpect(status().isOk())
                .andReturn();

        TestResponsePage<PlanetDto> page1 = pageJson.parseObject(mvcResult.getResponse().getContentAsString());
        assertEquals(1, page1.getContent().size());
        assertTrue( page1.getContent().get(0).getId() == 666L && page1.getContent().get(0).getTitle().equals(title));
    }
}