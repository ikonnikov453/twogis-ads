package com.example.twogisads.repository;

import com.example.twogisads.entity.Client;
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
@Import(JdbcClientRepository.class)
class JdbcClientRepositoryTest {

    @Autowired
    private JdbcClientRepository repository;

    @Autowired
    private DataSource dataSource;

    private int clientId; // реальный ID тестового клиента

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

            st.executeUpdate("CREATE TABLE IF NOT EXISTS placements (" +
                    "placement_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "client_id INT," +
                    "package_id INT," +
                    "start_date DATE," +
                    "end_date DATE," +
                    "monthly_payment DECIMAL(19,2))");

            // очищаем таблицы
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM placements")) { ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM clients")) { ps.executeUpdate(); }

            // вставляем тестового клиента и сохраняем ID
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO clients(legal_name, company_name, contact_person, phone, email) VALUES (?,?,?,?,?)",
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
    void findAll() {
        List<Client> clients = repository.findAll();
        assertEquals(1, clients.size());
        assertEquals("ООО тест", clients.get(0).getLegalName());
    }

    @Test
    void findById() {
        Optional<Client> c = repository.findById(clientId);
        assertTrue(c.isPresent());
        assertEquals("тест фирма", c.get().getCompanyName());
    }

    @Test
    void save() {
        Client newClient = new Client();
        newClient.setLegalName("ООО new");
        newClient.setCompanyName("c");
        newClient.setContactPerson("p");
        newClient.setPhone("+7");
        newClient.setEmail("e@e");
        Client saved = repository.save(newClient);
        assertNotNull(saved.getClientId());
        assertEquals("ООО new", saved.getLegalName());
    }

    @Test
    void update() {
        Client updated = new Client();
        updated.setLegalName("ООО обновлено");
        updated.setCompanyName("новая фирма");
        updated.setContactPerson("обновл");
        updated.setPhone("+7111");
        updated.setEmail("u@e.e");

        boolean ok = repository.update(clientId, updated);
        assertTrue(ok);

        Optional<Client> c = repository.findById(clientId);
        assertTrue(c.isPresent());
        assertEquals("ООО обновлено", c.get().getLegalName());
    }

    @Test
    void delete() {
        boolean ok = repository.delete(clientId);
        assertTrue(ok);
        assertTrue(repository.findById(clientId).isEmpty());
    }

    @Test
    void findByLegalName() {
        List<Client> res = repository.findByLegalName("тест");
        assertEquals(1, res.size());
    }
}
