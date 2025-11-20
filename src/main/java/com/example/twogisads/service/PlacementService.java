package com.example.twogisads.service;

import com.example.twogisads.entity.Placement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * сервис для размещений
 */
public interface PlacementService {
    List<Placement> getAll();
    Optional<Placement> getById(int id);
    Placement create(Placement placement);
    boolean update(int id, Placement placement);
    boolean delete(int id);
    List<Placement> findByPackageName(String packageName);
    List<Placement> findWithPaymentGreaterThan(BigDecimal value);
    List<Placement> findByClientId(int clientId);
}
