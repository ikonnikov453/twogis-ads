package com.example.twogisads.repository;

import com.example.twogisads.entity.AdPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcAdPackageRepository.class)
class JdbcAdPackageRepositoryTest {

    @Autowired private JdbcAdPackageRepository repository;
    @Autowired private DataSource dataSource;

    @BeforeEach
    void setup() throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {

            // Создаем таблицы, если их нет
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

            // Очищаем таблицы
            st.executeUpdate("DELETE FROM placements");
            st.executeUpdate("DELETE FROM ad_packages");

            // Вставляем тестовый пакет
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ad_packages(package_name, description, monthly_fee) VALUES (?,?,?)")) {
                ps.setString(1, "Старт медиа");
                ps.setString(2, "описание");
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(5000));
                ps.executeUpdate();
            }
        }
    }


    @Test
    void findAll() {
        List<AdPackage> list = repository.findAll();
        assertEquals(1, list.size());
        assertEquals("Старт медиа", list.get(0).getPackageName());
    }

    @Test
    void findById() {
        AdPackage p = repository.findAll().get(0);
        Optional<AdPackage> found = repository.findById(p.getPackageId());
        assertTrue(found.isPresent());
        assertEquals("Старт медиа", found.get().getPackageName());
    }

    @Test
    void update() {
        AdPackage p = repository.findAll().get(0); // получаем реальный ID
        AdPackage updated = new AdPackage();
        updated.setPackageName("Обновлено");
        updated.setDescription("новое описание");
        updated.setMonthlyFee(java.math.BigDecimal.valueOf(5555));

        boolean ok = repository.update(p.getPackageId(), updated);
        assertTrue(ok);

        Optional<AdPackage> check = repository.findById(p.getPackageId());
        assertTrue(check.isPresent());
        assertEquals("Обновлено", check.get().getPackageName());
    }


    @Test
    void save() {
        AdPackage p = new AdPackage();
        p.setPackageName("Новый пакет");
        p.setDescription("desc");
        p.setMonthlyFee(java.math.BigDecimal.valueOf(10000));
        AdPackage saved = repository.save(p);
        assertNotNull(saved.getPackageId());
    }


    @Test
    void delete() {
        AdPackage p = repository.findAll().get(0);
        boolean ok = repository.delete(p.getPackageId());
        assertTrue(ok);
        assertTrue(repository.findById(p.getPackageId()).isEmpty());
    }


    @Test
    void findByName() {
        List<AdPackage> list = repository.findByName("Старт");
        assertEquals(1, list.size());
    }
}
