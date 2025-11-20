package com.example.twogisads.service;

import com.example.twogisads.entity.Client;

import java.util.List;
import java.util.Optional;

/**
 * сервисный слой для клиентов
 */
public interface ClientService {
    List<Client> getAll();
    Optional<Client> getById(int id);
    Client create(Client client);
    boolean update(int id, Client client);
    boolean delete(int id);
    List<Client> searchByLegalName(String legalName);
}
