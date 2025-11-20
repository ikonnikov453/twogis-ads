package com.example.twogisads.service.impl;

import com.example.twogisads.entity.AdPackage;
import com.example.twogisads.repository.AdPackageRepository;
import com.example.twogisads.service.AdPackageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// реализация сервиса для ad_packages
@Service
public class AdPackageServiceImpl implements AdPackageService {

    private final AdPackageRepository repository;

    public AdPackageServiceImpl(AdPackageRepository repository) {
        this.repository = repository;
    }

    @Override public List<AdPackage> getAll(){ return repository.findAll(); }
    @Override public Optional<AdPackage> getById(int id){ return repository.findById(id); }
    @Override public AdPackage create(AdPackage adPackage){ return repository.save(adPackage); }
    @Override public boolean update(int id, AdPackage adPackage){ return repository.update(id, adPackage); }
    @Override public boolean delete(int id){ return repository.delete(id); }
    @Override public List<AdPackage> searchByName(String packageName){ return repository.findByName(packageName); }
}
