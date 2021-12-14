package io.solar.controller;

import io.solar.dto.ProductDto;
import io.solar.dto.Token;
import io.solar.dto.UserDto;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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
    private final String AUTH_TOKEN_HEADER_NAME = "auth_token";

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String authToken;
    private static String savedProductId;

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
    @Order(1)
    @Disabled
    public void getAllProducts_pageable_notEmptyPage() throws Exception {

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "0");
        queryParams.add("size", "10");

        MvcResult mvcResult = mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL)
                        .params(queryParams)
                        .header(AUTH_TOKEN_HEADER_NAME, authToken))
                .andExpect(status().isOk())
                .andReturn();

        Page<ProductDto> productDtoList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(10, productDtoList.getTotalElements());
    }

    @Test
    @Order(2)
    public void getProduct_productId_notEmptyResponse() throws Exception {

        String productId = "15";

        MvcResult mvcResult = mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL.concat(productId))
                        .header(AUTH_TOKEN_HEADER_NAME, authToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductDto productDtoResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Assertions.assertNotNull(productDtoResponse.getId());
    }

    @Test
    @Order(3)
    public void saveProduct_productDto_statusCodeIsOk() throws Exception {

        ProductDto productDto = ProductDto.builder()
                .bulk(0.1f).mass(1500f)
                .title("Test product").price(15.99f)
                .build();

        mockMvc
                .perform(post(PRODUCT_CONTROLLER_URL)
                        .header(AUTH_TOKEN_HEADER_NAME, authToken)
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo((res) -> {
                    ProductDto resp = objectMapper.readValue(res.getResponse().getContentAsString(), ProductDto.class);

                    mockMvc
                            .perform(get(PRODUCT_CONTROLLER_URL.concat(resp.getId().toString()))
                                    .header(AUTH_TOKEN_HEADER_NAME, authToken))
                            .andExpect(status().isOk());

                    savedProductId = resp.getId().toString();
                });
    }

    @Test
    @Order(4)
    public void deleteProduct_productId_statusCodeIsOk() throws Exception {

        mockMvc
                .perform(delete(PRODUCT_CONTROLLER_URL.concat(savedProductId))
                        .header(AUTH_TOKEN_HEADER_NAME, authToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void dropdown_statusCodeIsOk() throws Exception {
        mockMvc
                .perform(get(PRODUCT_CONTROLLER_URL.concat("utils/dropdown"))
                        .header(AUTH_TOKEN_HEADER_NAME, authToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
