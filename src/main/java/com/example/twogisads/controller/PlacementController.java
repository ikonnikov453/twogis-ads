package com.example.twogisads.controller;

import com.example.twogisads.entity.Placement;
import com.example.twogisads.service.PlacementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

// REST контроллер для размещений
@RestController
@RequestMapping("/placements")
public class PlacementController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping
    public List<Placement> getAll() {
        return placementService.getAll();
    }

    // Только числовые id, чтобы не конфликтовать с /search и другими строковыми путями
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Placement> getById(@PathVariable int id) {
        return placementService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Placement> create(@RequestBody Placement placement) {
        return ResponseEntity.ok(placementService.create(placement));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Placement> update(@PathVariable int id, @RequestBody Placement placement) {
        placement.setPlacementId(id);
        boolean updated = placementService.update(id, placement);
        if (updated) {
            return ResponseEntity.ok(placementService.getById(id).orElse(null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return placementService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/byPackage")
    public List<Placement> findByPackageName(@RequestParam String packageName) {
        return placementService.findByPackageName(packageName);
    }

    @GetMapping("/withPaymentGreaterThan")
    public List<Placement> findWithPaymentGreaterThan(@RequestParam double value) {
        return placementService.findWithPaymentGreaterThan(BigDecimal.valueOf(value));
    }

    @GetMapping("/search")
    public List<Placement> findByClient(@RequestParam int clientId) {
        return placementService.findByClientId(clientId);
    }
}
