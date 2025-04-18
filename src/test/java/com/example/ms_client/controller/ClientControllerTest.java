package com.example.ms_client.controller;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.entity.ClientType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    public void clearDatabase() throws Exception {
        mockMvc.perform(delete("/client"))
                .andExpect(status().isNoContent());
    }

    private ClientDTO buildValidClientDTO(String nameSuffix, boolean active) {
        String time = LocalDateTime.now().format(FORMATTER);
        return new ClientDTO(
                null,
                "ООО Тестовый Клиент " + nameSuffix,
                "ТК" + nameSuffix,
                UUID.randomUUID().toString().substring(0, 10),
                active,
                time,
                time,
                ClientType.UL.name()
        );
    }

    private UUID createClientAndReturnId(ClientDTO dto) throws Exception {
        String response = mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, ClientDTO.class).getId();
    }

    @Test
    public void testCreateClient_success() throws Exception {
        ClientDTO dto = buildValidClientDTO("CreateSuccess", true);
        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value(dto.getFullName()));
    }

    @Test
    public void testCreateClient_validationError() throws Exception {
        ClientDTO dto = new ClientDTO(null, "", "", "", true, "", "", "");

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    public void testGetClientById_success() throws Exception {
        ClientDTO dto = buildValidClientDTO("GetById", true);
        UUID id = createClientAndReturnId(dto);

        mockMvc.perform(get("/client/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    public void testGetClientById_notFound() throws Exception {
        mockMvc.perform(get("/client/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateClient_success() throws Exception {
        ClientDTO dto = buildValidClientDTO("UpdateSuccess", true);
        UUID id = createClientAndReturnId(dto);

        dto.setId(id);
        dto.setFullName("ООО Обновлённый Тест");

        mockMvc.perform(put("/client/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("ООО Обновлённый Тест"));
    }

    @Test
    public void testUpdateClient_notFound() throws Exception {
        ClientDTO dto = buildValidClientDTO("NotFound", true);

        mockMvc.perform(put("/client/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateClient_notActive() throws Exception {
        ClientDTO dto = buildValidClientDTO("InactiveUpdate", false);
        UUID id = createClientAndReturnId(dto);
        dto.setId(id);

        mockMvc.perform(put("/client/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testGetClientsPaginated_withSorting() throws Exception {
        ClientDTO first = buildValidClientDTO("A", true);
        Thread.sleep(100); // Чтобы createDateTime отличалось
        ClientDTO second = buildValidClientDTO("B", true);

        createClientAndReturnId(first);
        createClientAndReturnId(second);

        mockMvc.perform(get("/client")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].createDateTime", notNullValue()))
                .andExpect(jsonPath("$[1].createDateTime", notNullValue()));
    }

    @Test
    public void testSearchClientsBySymbol() throws Exception {
        ClientDTO dto = buildValidClientDTO("Тест", true);
        createClientAndReturnId(dto);

        mockMvc.perform(get("/client/search")
                        .param("querySymbol", "Тест"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem(hasEntry("fullName", dto.getFullName()))));
    }

    @Test
    public void testDeleteClientById_success() throws Exception {
        ClientDTO dto = buildValidClientDTO("ToDelete", true);
        UUID id = createClientAndReturnId(dto);

        mockMvc.perform(delete("/client/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/client/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAllClients() throws Exception {
        createClientAndReturnId(buildValidClientDTO("DelAll1", true));
        createClientAndReturnId(buildValidClientDTO("DelAll2", true));

        mockMvc.perform(delete("/client"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetClientById_inactiveClient() throws Exception {
        ClientDTO dto = buildValidClientDTO("Inactive", false);
        UUID id = createClientAndReturnId(dto);

        mockMvc.perform(get("/client/" + id))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testFilterClientsByActiveStatus() throws Exception {
        createClientAndReturnId(buildValidClientDTO("Active1", true));
        createClientAndReturnId(buildValidClientDTO("Inactive1", false));

        mockMvc.perform(get("/client")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].active", everyItem(is(true))));

        mockMvc.perform(get("/client")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].active", everyItem(is(false))));
    }

    @Test
    public void testCreateClient_duplicateInn_shouldFail() throws Exception {
        String inn = "1234567890";
        ClientDTO dto1 = buildValidClientDTO("DUP1", true);
        dto1.setInn(inn);

        ClientDTO dto2 = buildValidClientDTO("DUP2", true);
        dto2.setInn(inn);

        createClientAndReturnId(dto1);

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("уже существует")));
    }

    @Test
    public void testUpdateClient_invalidId_shouldReturnNotFound() throws Exception {
        ClientDTO dto = buildValidClientDTO("InvalidIdUpdate", true);
        dto.setId(UUID.randomUUID());

        mockMvc.perform(put("/client/" + dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

}