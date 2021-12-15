package io.solar.controller;

import io.solar.dto.ProductDto;
import io.solar.utils.PageResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    private final String PRODUCT_CONTROLLER_URL = "/api/product/";

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String savedProductId;

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PRODUCT"})
    public void getAllProducts_pageable_notEmptyPage() throws Exception {

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "10");

        MvcResult mvcResult = mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL)
                        .params(queryParams))
                .andExpect(status().isOk())
                .andReturn();

        PageResponse<ProductDto> productDtoList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<PageResponse<ProductDto>>() {});

        Assertions.assertEquals(10, productDtoList.getNumberOfElements());
    }

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PRODUCT"})
    public void getProduct_productId_notEmptyResponse() throws Exception {

        String productId = "15";

        MvcResult mvcResult = mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL.concat(productId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductDto productDtoResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(productDtoResponse.getId());
    }

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PRODUCT"})
    public void saveProduct_productDto_statusCodeIsOk() throws Exception {

        ProductDto productDto = ProductDto.builder()
                .bulk(0.1f).mass(1500f)
                .title("Test product").price(15.99f)
                .build();

        mockMvc
                .perform(post(PRODUCT_CONTROLLER_URL)
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo((res) -> {
                    ProductDto resp = objectMapper.readValue(res.getResponse().getContentAsString(), ProductDto.class);

                    mockMvc
                            .perform(get(PRODUCT_CONTROLLER_URL.concat(resp.getId().toString())))
                            .andExpect(status().isOk());

                    savedProductId = resp.getId().toString();
                });
    }

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PRODUCT"})
    public void deleteProduct_productId_statusCodeIsOk() throws Exception {

        mockMvc
                .perform(delete(PRODUCT_CONTROLLER_URL.concat(savedProductId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"PLAY_THE_GAME", "EDIT_PRODUCT"})
    public void dropdown_statusCodeIsOk() throws Exception {
        mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL.concat("utils/dropdown"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
