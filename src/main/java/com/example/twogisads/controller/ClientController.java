package com.example.twogisads.controller;

import com.example.twogisads.entity.Client;
import com.example.twogisads.entity.Placement;
import com.example.twogisads.service.ClientService;
import com.example.twogisads.service.PlacementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// rest контроллер для клиентов
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final PlacementService placementService;

    public ClientController(ClientService clientService, PlacementService placementService) {
        this.clientService = clientService;
        this.placementService = placementService;
    }

    // получить всех клиентов
    @GetMapping
    public List<Client> getAll() {
        return clientService.getAll();
    }

    // получить клиента по id
    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable int id) {
        return clientService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // создать нового клиента
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client created = clientService.create(client);
        return ResponseEntity.ok(created);
    }

    // обновить существующего клиента
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable int id, @RequestBody Client client) {
        client.setClientId(id);
        boolean updated = clientService.update(client.getClientId(), client);
        if (updated) {
            // возвращаем обновленный объект клиента
            Client updatedClient = clientService.getById(id).orElse(null);
            return ResponseEntity.ok(updatedClient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // удалить клиента
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean deleted = clientService.delete(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // поиск клиентов по юридическому названию
    @GetMapping("/search")
    public List<Client> searchByLegalName(@RequestParam String legalName) {
        return clientService.searchByLegalName(legalName);
    }

    // получить все размещения конкретного клиента
    @GetMapping("/{id}/placements")
    public List<Placement> getPlacementsByClient(@PathVariable int id) {
        return placementService.findByClientId(id);
    }
}
