package com.example.twogisads.repository;

import com.example.twogisads.entity.AdPackage;

import java.util.List;
import java.util.Optional;

// репозиторий для рекламных пакетов
public interface AdPackageRepository {
    List<AdPackage> findAll();
    Optional<AdPackage> findById(int id);
    AdPackage save(AdPackage adPackage);
    boolean update(int id, AdPackage adPackage);
    boolean delete(int id);
    List<AdPackage> findByName(String packageName);
}
