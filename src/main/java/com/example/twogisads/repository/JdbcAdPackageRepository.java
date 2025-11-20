package com.example.twogisads.repository;

import com.example.twogisads.entity.AdPackage;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// реализация репозитория ad_packages на чистом jdbc
@Repository
public class JdbcAdPackageRepository implements AdPackageRepository {

    private final DataSource dataSource;

    public JdbcAdPackageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<AdPackage> findAll() {
        String sql = "select package_id, package_name, description, monthly_fee from ad_packages";
        List<AdPackage> result = new ArrayList<>();
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
    public Optional<AdPackage> findById(int id) {
        String sql = "select package_id, package_name, description, monthly_fee from ad_packages where package_id = ?";
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
    public AdPackage save(AdPackage adPackage) {
        String sql = "insert into ad_packages(package_name, description, monthly_fee) values (?,?,?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, adPackage.getPackageName());
            ps.setString(2, adPackage.getDescription());
            ps.setBigDecimal(3, adPackage.getMonthlyFee());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) adPackage.setPackageId(keys.getInt(1));
            }
            return adPackage;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(int id, AdPackage adPackage) {
        String sql = "update ad_packages set package_name = ?, description = ?, monthly_fee = ? where package_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, adPackage.getPackageName());
            ps.setString(2, adPackage.getDescription());
            ps.setBigDecimal(3, adPackage.getMonthlyFee());
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        // при удалении пакета удаляем связанные размещения сначала
        String delPlacements = "delete from placements where package_id = ?";
        String delPackage = "delete from ad_packages where package_id = ?";
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(delPlacements)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(delPackage)) {
                ps2.setInt(1, id);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AdPackage> findByName(String packageName) {
        String sql = "select package_id, package_name, description, monthly_fee from ad_packages where lower(package_name) like ?";
        List<AdPackage> result = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + packageName.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private AdPackage mapRow(ResultSet rs) throws SQLException {
        AdPackage p = new AdPackage();
        p.setPackageId(rs.getInt("package_id"));
        p.setPackageName(rs.getString("package_name"));
        p.setDescription(rs.getString("description"));
        p.setMonthlyFee(rs.getBigDecimal("monthly_fee"));
        return p;
    }
}
