package io.solar.controller;

import io.solar.Start;
import io.solar.dto.PlanetDto;
import io.solar.dto.Token;
import io.solar.dto.UserDto;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = {Start.class})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String authToken;

    @BeforeEach
    public void getAuthToken() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setLogin("admin");
        userDto.setPassword("admin");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        Token token = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Token.class);

        authToken = token.getData();
    }

    @Test
    void findById_shouldReturnPlanetByCurrentId() throws Exception {
        int planetId = 500;
        MvcResult mvcResult = mockMvc.perform(get("/api/planet/{id}", planetId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        PlanetDto dto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PlanetDto.class);
        System.out.println("!!!!!!!!!!!!!!!" + dto.getId());
    }
}
