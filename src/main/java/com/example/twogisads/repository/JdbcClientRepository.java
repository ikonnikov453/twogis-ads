package com.example.twogisads.repository;

import com.example.twogisads.entity.Client;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// реализация репозитория клиентов на чистом jdbc
@Repository
public class JdbcClientRepository implements ClientRepository {

    private final DataSource dataSource;

    public JdbcClientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Client> findAll() {
        String sql = "select client_id, legal_name, company_name, contact_person, phone, email from clients";
        List<Client> result = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Optional<Client> findById(int id) {
        String sql = "select client_id, legal_name, company_name, contact_person, phone, email from clients where client_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Client save(Client client) {
        String sql = "insert into clients(legal_name, company_name, contact_person, phone, email) values (?,?,?,?,?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, client.getLegalName());
            ps.setString(2, client.getCompanyName());
            ps.setString(3, client.getContactPerson());
            ps.setString(4, client.getPhone());
            ps.setString(5, client.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) client.setClientId(keys.getInt(1));
            }
            return client;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(int id, Client client) {
        String sql = "update clients set legal_name = ?, company_name = ?, contact_person = ?, phone = ?, email = ? where client_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, client.getLegalName());
            ps.setString(2, client.getCompanyName());
            ps.setString(3, client.getContactPerson());
            ps.setString(4, client.getPhone());
            ps.setString(5, client.getEmail());
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        // сначала удаляем связанные размещения, чтобы избежать ошибок foreign key
        String delPlacements = "delete from placements where client_id = ?";
        String delClient = "delete from clients where client_id = ?";
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(delPlacements)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(delClient)) {
                ps2.setInt(1, id);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Client> findByLegalName(String legalName) {
        String sql = "select client_id, legal_name, company_name, contact_person, phone, email from clients where lower(legal_name) like ?";
        List<Client> result = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + legalName.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        Client cl = new Client();
        cl.setClientId(rs.getInt("client_id"));
        cl.setLegalName(rs.getString("legal_name"));
        cl.setCompanyName(rs.getString("company_name"));
        cl.setContactPerson(rs.getString("contact_person"));
        cl.setPhone(rs.getString("phone"));
        cl.setEmail(rs.getString("email"));
        return cl;
    }
}
