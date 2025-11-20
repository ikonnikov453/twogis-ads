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
class AdPackageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DataSource dataSource;

    private int packageId;

    @BeforeEach
    void setup() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM ad_packages")) {
                ps.executeUpdate();
            }
            // вставляем пакет и получаем сгенерированный ID
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ad_packages(package_name, description, monthly_fee) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Старт медиа");
                ps.setString(2, "описание");
                ps.setDouble(3, 5000);
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        packageId = rs.getInt(1);
                    }
                }
            }
        }
    }


    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/adPackages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageName").value("Старт медиа"));
    }

    @Test
    void testCreate() throws Exception {
        String json = "{\"packageName\":\"Стандарт медиа\",\"description\":\"desc\",\"monthlyFee\":10000}";
        mockMvc.perform(post("/adPackages")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageId").isNumber());
    }

    @Test
    void testUpdate() throws Exception {
        String json = "{\"packageName\":\"Старт обновлено\",\"description\":\"новое описание\",\"monthlyFee\":6000}";
        mockMvc.perform(put("/adPackages/" + packageId)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageName").value("Старт обновлено"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/adPackages/" + packageId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/adPackages/" + packageId))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSearchByName() throws Exception {
        mockMvc.perform(get("/adPackages/search?name=Старт"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageName").value("Старт медиа"));
    }
}
