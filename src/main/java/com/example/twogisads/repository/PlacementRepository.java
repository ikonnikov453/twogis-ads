package com.example.twogisads.repository;

import com.example.twogisads.entity.Placement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// репозиторий для размещений
public interface PlacementRepository {
    List<Placement> findAll();
    Optional<Placement> findById(int id);
    Placement save(Placement placement);
    boolean update(int id, Placement placement);
    boolean delete(int id);
    List<Placement> findByPackageName(String packageName);
    List<Placement> findWithPaymentGreaterThan(BigDecimal value);
    List<Placement> findByClientId(int clientId);
}
