package com.example.twogisads.service.impl;

import com.example.twogisads.entity.Client;
import com.example.twogisads.repository.ClientRepository;
import com.example.twogisads.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// реализация сервисного слоя клиентов
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Override public List<Client> getAll(){ return repository.findAll(); }
    @Override public Optional<Client> getById(int id){ return repository.findById(id); }
    @Override public Client create(Client client){ return repository.save(client); }
    @Override public boolean update(int id, Client client){ return repository.update(id, client); }
    @Override public boolean delete(int id){ return repository.delete(id); }
    @Override public List<Client> searchByLegalName(String legalName){ return repository.findByLegalName(legalName); }
}
