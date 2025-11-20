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
import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestDatabaseInitializer.class)
@ActiveProfiles("test")
class PlacementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    private int placementId;
    private int clientId;
    private int packageId;

    @BeforeEach
    void setup() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            // очищаем таблицы
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM placements")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM clients")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM ad_packages")) { ps.executeUpdate(); }

            // создаём тестового клиента
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO clients(legal_name, company_name, contact_person, phone, email) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "ООО тест");
                ps.setString(2, "Тест фирма");
                ps.setString(3, "Тестович");
                ps.setString(4, "+70000000000");
                ps.setString(5, "t@test.local");
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) clientId = rs.getInt(1);
                }
            }

            // создаём тестовый пакет
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ad_packages(package_name, description, monthly_fee) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Старт медиа");
                ps.setString(2, "описание");
                ps.setDouble(3, 5000);
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) packageId = rs.getInt(1);
                }
            }

            // создаём тестовое размещение
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO placements(client_id, package_id, start_date, end_date, monthly_payment) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clientId);
                ps.setInt(2, packageId);
                ps.setDate(3, Date.valueOf("2025-01-01"));
                ps.setDate(4, Date.valueOf("2025-12-31"));
                ps.setDouble(5, 5000);
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) placementId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void testGetAllPlacements() throws Exception {
        mockMvc.perform(get("/placements"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].placementId").value(placementId));
    }

    @Test
    void testGetPlacementById() throws Exception {
        mockMvc.perform(get("/placements/" + placementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placementId").value(placementId));
    }

    @Test
    void testCreatePlacement() throws Exception {
        String json = String.format(
                "{\"clientId\":%d,\"packageId\":%d,\"startDate\":\"2025-02-01\",\"endDate\":\"2025-12-31\",\"monthlyPayment\":6000}",
                clientId, packageId);
        mockMvc.perform(post("/placements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placementId").isNumber())
                .andExpect(jsonPath("$.monthlyPayment").value(6000));
    }

    @Test
    void testUpdatePlacement() throws Exception {
        String json = String.format(
                "{\"clientId\":%d,\"packageId\":%d,\"startDate\":\"2025-03-01\",\"endDate\":\"2025-12-31\",\"monthlyPayment\":7000}",
                clientId, packageId);
        mockMvc.perform(put("/placements/" + placementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPayment").value(7000));
    }

    @Test
    void testDeletePlacement() throws Exception {
        mockMvc.perform(delete("/placements/" + placementId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/placements/" + placementId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchPlacementsByClient() throws Exception {
        mockMvc.perform(get("/placements/search?clientId=" + clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placementId").value(placementId));
    }
}
