package com.example.twogisads.repository;

import com.example.twogisads.entity.Client;

import java.util.List;
import java.util.Optional;

// репозиторий для работы с клиентами
public interface ClientRepository {
    List<Client> findAll();
    Optional<Client> findById(int id);
    Client save(Client client);
    boolean update(int id, Client client);
    boolean delete(int id);
    List<Client> findByLegalName(String legalName);
}
