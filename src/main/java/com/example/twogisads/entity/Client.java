package com.example.twogisads.entity;

import java.util.Objects;

/**
 * сущность клиент (clients)
 */
public class Client {
    // идентификатор клиента
    private Integer clientId;
    // юридическое лицо
    private String legalName;
    // название фирмы
    private String companyName;
    // контактное лицо
    private String contactPerson;
    // телефон
    private String phone;
    // электронная почта
    private String email;

    public Client() {}

    public Client(Integer clientId, String legalName, String companyName, String contactPerson, String phone, String email) {
        this.clientId = clientId;
        this.legalName = legalName;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
    }

    // геттеры и сеттеры
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
