package com.example.twogisads.repository;

import com.example.twogisads.entity.Placement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcPlacementRepository.class)
class JdbcPlacementRepositoryTest {

    @Autowired private JdbcPlacementRepository repository;
    @Autowired private DataSource dataSource;

    private int clientId;
    private int packageId;
    private int placementId;

    @BeforeEach
    void setup() throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {

            // создаём таблицы, если их нет
            st.executeUpdate("CREATE TABLE IF NOT EXISTS clients (" +
                    "client_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "legal_name VARCHAR(255)," +
                    "company_name VARCHAR(255)," +
                    "contact_person VARCHAR(255)," +
                    "phone VARCHAR(50)," +
                    "email VARCHAR(255))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS ad_packages (" +
                    "package_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "package_name VARCHAR(255)," +
                    "description VARCHAR(1000)," +
                    "monthly_fee DECIMAL(19,2))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS placements (" +
                    "placement_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "client_id INT," +
                    "package_id INT," +
                    "start_date DATE," +
                    "end_date DATE," +
                    "monthly_payment DECIMAL(19,2))");

            // очищаем таблицы
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM placements")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM ad_packages")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM clients")) { ps.executeUpdate(); }

            // вставляем тестового клиента
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO clients(legal_name, company_name, contact_person, phone, email) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,"ООО тест");
                ps.setString(2,"тест фирма");
                ps.setString(3,"тестович");
                ps.setString(4,"+70000000000");
                ps.setString(5,"t@test.local");
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) clientId = rs.getInt(1);
                }
            }

            // вставляем тестовый пакет
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ad_packages(package_name, description, monthly_fee) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,"Старт медиа");
                ps.setString(2,"описание");
                ps.setBigDecimal(3, BigDecimal.valueOf(5000));
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) packageId = rs.getInt(1);
                }
            }

            // вставляем тестовое размещение
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO placements(client_id, package_id, start_date, end_date, monthly_payment) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clientId);
                ps.setInt(2, packageId);
                ps.setDate(3, java.sql.Date.valueOf("2025-09-11"));
                ps.setDate(4, java.sql.Date.valueOf("2026-09-11"));
                ps.setBigDecimal(5, BigDecimal.valueOf(5500));
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) placementId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void findAll() {
        List<Placement> list = repository.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void findById() {
        Optional<Placement> p = repository.findById(placementId);
        assertTrue(p.isPresent());
        // обработка несовпадающих типов
        assertTrue(p.get().getMonthlyPayment().compareTo(BigDecimal.valueOf(5500)) == 0);


    }

    @Test
    void save() {
        Placement p = new Placement();
        p.setClientId(clientId);
        p.setPackageId(packageId);
        p.setStartDate(LocalDate.now());
        p.setEndDate(LocalDate.now().plusDays(10));
        p.setMonthlyPayment(BigDecimal.valueOf(10000));
        Placement saved = repository.save(p);
        assertNotNull(saved.getPlacementId());
    }

    @Test
    void update() {
        Placement p = new Placement();
        p.setClientId(clientId);
        p.setPackageId(packageId);
        p.setStartDate(LocalDate.now());
        p.setEndDate(LocalDate.now().plusDays(20));
        p.setMonthlyPayment(BigDecimal.valueOf(5000));
        boolean ok = repository.update(placementId, p);
        assertTrue(ok);
        Optional<Placement> check = repository.findById(placementId);
        assertTrue(check.get().getMonthlyPayment().compareTo(BigDecimal.valueOf(5000)) == 0);


    }

    @Test
    void delete() {
        boolean ok = repository.delete(placementId);
        assertTrue(ok);
        assertTrue(repository.findById(placementId).isEmpty());
    }

    @Test
    void findByPackageName() {
        List<Placement> list = repository.findByPackageName("Старт");
        assertEquals(1, list.size());
    }

    @Test
    void findWithPaymentGreaterThan() {
        List<Placement> list = repository.findWithPaymentGreaterThan(BigDecimal.valueOf(500));
        assertEquals(1, list.size());
    }

    @Test
    void findByClientId() {
        List<Placement> list = repository.findByClientId(clientId);
        assertEquals(1, list.size());
    }
}
