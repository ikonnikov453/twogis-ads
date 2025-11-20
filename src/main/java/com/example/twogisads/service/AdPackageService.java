package com.example.twogisads.service;

import com.example.twogisads.entity.AdPackage;

import java.util.List;
import java.util.Optional;

/**
 * сервис для рекламных пакетов
 */
public interface AdPackageService {
    List<AdPackage> getAll();
    Optional<AdPackage> getById(int id);
    AdPackage create(AdPackage adPackage);
    boolean update(int id, AdPackage adPackage);
    boolean delete(int id);
    List<AdPackage> searchByName(String packageName);
}
