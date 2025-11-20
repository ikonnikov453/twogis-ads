package com.example.twogisads.controller;

import com.example.twogisads.TestDatabaseInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestDatabaseInitializer.class)
@ActiveProfiles("test")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    private int clientId;

    @BeforeEach
    void setup() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            // очищаем таблицы перед каждым тестом
            try (PreparedStatement ps = c.prepareStatement("delete from placements")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("delete from clients")) { ps.executeUpdate(); }

            // вставляем тестового клиента и получаем его id
            try (PreparedStatement ps = c.prepareStatement(
                    "insert into clients(legal_name, company_name, contact_person, phone, email) values (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "ООО тест");
                ps.setString(2, "тест фирма");
                ps.setString(3, "тестович");
                ps.setString(4, "+70000000000");
                ps.setString(5, "t@test.local");
                ps.executeUpdate();

                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        clientId = rs.getInt(1);
                    }
                }
            }
        }
    }

    @Test
    void testGetAllClients() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].legalName").value("ООО тест"));
    }

    @Test
    void testCreateClient() throws Exception {
        String json = "{\"legalName\":\"ООО new\",\"companyName\":\"c\",\"contactPerson\":\"p\",\"phone\":\"+7\",\"email\":\"e@e\"}";
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").isNumber())
                .andExpect(jsonPath("$.legalName").value("ООО new"));
    }

    @Test
    void testUpdateClient() throws Exception {
        String json = "{\"legalName\":\"ООО обновлено\",\"companyName\":\"новая фирма\",\"contactPerson\":\"обновл\",\"phone\":\"+7111\",\"email\":\"u@e.e\"}";
        mockMvc.perform(put("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalName").value("ООО обновлено"))
                .andExpect(jsonPath("$.companyName").value("новая фирма"));
    }

    @Test
    void testDeleteClient() throws Exception {
        mockMvc.perform(delete("/clients/" + clientId))
                .andExpect(status().isOk());

        // проверяем, что клиент удалён
        mockMvc.perform(get("/clients/" + clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchByLegalName() throws Exception {
        mockMvc.perform(get("/clients/search?legalName=тест"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].legalName").value("ООО тест"));
    }

    @Test
    void testGetClientPlacementsEmpty() throws Exception {
        mockMvc.perform(get("/clients/" + clientId + "/placements"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
