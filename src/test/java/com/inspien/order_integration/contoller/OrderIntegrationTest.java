package com.inspien.order_integration.contoller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("XML 주문 요청이 오면 DB에 성공적으로 적재되어야 한다")
    void shouldProcessOrderSuccessfully() throws Exception {
        // 1. 요청할 XML (루트 태그로 감싸야 함)
        String xmlContent = """
            <ROOT>
                <HEADER>
                    <USER_ID>USER1</USER_ID>
                    <NAME>홍길동</NAME>
                    <ADDRESS>서울특별시 금천구</ADDRESS>
                    <STATUS>N</STATUS>
                </HEADER>
                <HEADER>
                    <USER_ID>USER2</USER_ID>
                    <NAME>유관순</NAME>
                    <ADDRESS>서울특별시 구로구</ADDRESS>
                    <STATUS>N</STATUS>
                </HEADER>
                <ITEM>
                    <USER_ID>USER1</USER_ID>
                    <ITEM_ID>ITEM1</ITEM_ID>
                    <ITEM_NAME>청바지</ITEM_NAME>
                    <PRICE>21000</PRICE>
                </ITEM>
                <ITEM>
                    <USER_ID>USER2</USER_ID>
                    <ITEM_ID>ITEM2</ITEM_ID>
                    <ITEM_NAME>티셔츠</ITEM_NAME>
                    <PRICE>15800</PRICE>
                </ITEM>
            </ROOT>
            """;

        // 2. API 호출 및 검증
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"));
    }
}