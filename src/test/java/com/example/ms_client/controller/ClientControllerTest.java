package com.example.msclient.controller;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.entity.ClientType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createClient_success() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "Full Name",
                "Short Name",
                ClientType.IP,
                "123456789012",
                true
        );

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createClient_invalidFields() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "", // fullName пустой
                "Short Name",
                ClientType.IP,
                "123", // невалидный ИНН
                true
        );

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateClient_success() throws Exception {
        // Сначала создаём клиента
        ClientRequestDto request = new ClientRequestDto(
                "Full Name",
                "Short Name",
                ClientType.UL,
                "12345678901234",
                true
        );

        String response = mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Обновляем клиента
        request = new ClientRequestDto("New Full Name", "Short", ClientType.UL, "12345678901234", true);

        mockMvc.perform(put("/client/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Full Name"));
    }

    @Test
    void updateClient_notFound() throws Exception {
        ClientRequestDto request = new ClientRequestDto("X", "Y", ClientType.IP, "123456789012", true);

        mockMvc.perform(put("/client/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClient_inactiveClient() throws Exception {
        // Сначала создаём клиента
        ClientRequestDto request = new ClientRequestDto(
                "Inactive",
                "Client",
                ClientType.IP,
                "123456789012",
                false
        );

        String response = mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Пытаемся обновить
        request = new ClientRequestDto("Should Fail", "X", ClientType.IP, "123456789012", false);

        mockMvc.perform(put("/client/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getClientById_success() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "FindMe",
                "X",
                ClientType.IP,
                "123456789012",
                true
        );

        String response = mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/client/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("FindMe"));
    }

    @Test
    void getClientById_notFound() throws Exception {
        mockMvc.perform(get("/client/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllClients_pagination() throws Exception {
        mockMvc.perform(get("/client?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void searchClient_success() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "TestSearch",
                "Short",
                ClientType.IP,
                "123456789012",
                true
        );

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/client/search?querySymbol=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("TestSearch"));
    }

    @Test
    void searchClient_notFound() throws Exception {
        mockMvc.perform(get("/client/search?querySymbol=NoMatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
