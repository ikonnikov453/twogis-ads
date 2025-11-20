package com.example.twogisads.repository;

import com.example.twogisads.entity.Placement;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

// реализация репозитория placements на чистом jdbc
@Repository
public class JdbcPlacementRepository implements PlacementRepository {

    private final DataSource dataSource;

    public JdbcPlacementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Placement> findAll() {
        String sql = "select placement_id, client_id, package_id, start_date, end_date, monthly_payment from placements";
        List<Placement> res = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) res.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public Optional<Placement> findById(int id) {
        String sql = "select placement_id, client_id, package_id, start_date, end_date, monthly_payment from placements where placement_id = ?";
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
    public Placement save(Placement placement) {
        String sql = "insert into placements(client_id, package_id, start_date, end_date, monthly_payment) values (?,?,?,?,?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, placement.getClientId());
            ps.setInt(2, placement.getPackageId());
            ps.setDate(3, placement.getStartDate() == null ? null : Date.valueOf(placement.getStartDate()));
            ps.setDate(4, placement.getEndDate() == null ? null : Date.valueOf(placement.getEndDate()));
            ps.setBigDecimal(5, placement.getMonthlyPayment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) placement.setPlacementId(keys.getInt(1));
            }
            return placement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(int id, Placement placement) {
        String sql = "update placements set client_id = ?, package_id = ?, start_date = ?, end_date = ?, monthly_payment = ? where placement_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, placement.getClientId());
            ps.setInt(2, placement.getPackageId());
            ps.setDate(3, placement.getStartDate() == null ? null : Date.valueOf(placement.getStartDate()));
            ps.setDate(4, placement.getEndDate() == null ? null : Date.valueOf(placement.getEndDate()));
            ps.setBigDecimal(5, placement.getMonthlyPayment());
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "delete from placements where placement_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Placement> findByPackageName(String packageName) {
        String sql = "select p.placement_id, p.client_id, p.package_id, p.start_date, p.end_date, p.monthly_payment " +
                "from placements p join ad_packages a on p.package_id = a.package_id where lower(a.package_name) like ?";
        List<Placement> res = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + packageName.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public List<Placement> findWithPaymentGreaterThan(BigDecimal value) {
        String sql = "select placement_id, client_id, package_id, start_date, end_date, monthly_payment from placements where monthly_payment > ?";
        List<Placement> res = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public List<Placement> findByClientId(int clientId) {
        String sql = "select placement_id, client_id, package_id, start_date, end_date, monthly_payment from placements where client_id = ?";
        List<Placement> res = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private Placement mapRow(ResultSet rs) throws SQLException {
        Placement p = new Placement();
        p.setPlacementId(rs.getInt("placement_id"));
        p.setClientId(rs.getInt("client_id"));
        p.setPackageId(rs.getInt("package_id"));
        Date sd = rs.getDate("start_date");
        Date ed = rs.getDate("end_date");
        if (sd != null) p.setStartDate(sd.toLocalDate());
        if (ed != null) p.setEndDate(ed.toLocalDate());
        p.setMonthlyPayment(rs.getBigDecimal("monthly_payment"));
        return p;
    }
}
