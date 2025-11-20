package com.example.twogisads.service.impl;

import com.example.twogisads.entity.Placement;
import com.example.twogisads.repository.PlacementRepository;
import com.example.twogisads.service.PlacementService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// реализация сервиса placements
@Service
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository repository;

    public PlacementServiceImpl(PlacementRepository repository) {
        this.repository = repository;
    }

    @Override public List<Placement> getAll(){ return repository.findAll(); }
    @Override public Optional<Placement> getById(int id){ return repository.findById(id); }
    @Override public Placement create(Placement placement){ return repository.save(placement); }
    @Override public boolean update(int id, Placement placement){ return repository.update(id, placement); }
    @Override public boolean delete(int id){ return repository.delete(id); }
    @Override public List<Placement> findByPackageName(String packageName){ return repository.findByPackageName(packageName); }
    @Override public List<Placement> findWithPaymentGreaterThan(BigDecimal value){ return repository.findWithPaymentGreaterThan(value); }
    @Override public List<Placement> findByClientId(int clientId){ return repository.findByClientId(clientId); }
}
