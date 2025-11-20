package com.example.twogisads.controller;

import com.example.twogisads.entity.AdPackage;
import com.example.twogisads.service.AdPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// rest контроллер для рекламных пакетов
@RestController
@RequestMapping("/adPackages")
public class AdPackageController {

    private final AdPackageService adPackageService;

    public AdPackageController(AdPackageService adPackageService) {
        this.adPackageService = adPackageService;
    }

    @GetMapping
    public List<AdPackage> getAll() {
        return adPackageService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdPackage> getById(@PathVariable int id) {
        return adPackageService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdPackage> create(@RequestBody AdPackage adPackage) {
        return ResponseEntity.ok(adPackageService.create(adPackage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdPackage> update(@PathVariable int id, @RequestBody AdPackage adPackage) {
        adPackage.setPackageId(id);
        boolean updated = adPackageService.update(adPackage.getPackageId(), adPackage);
        if (updated) {
            return ResponseEntity.ok(adPackageService.getById(id).orElse(null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return adPackageService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<AdPackage> searchByName(@RequestParam String name) {
        return adPackageService.searchByName(name);
    }
}
