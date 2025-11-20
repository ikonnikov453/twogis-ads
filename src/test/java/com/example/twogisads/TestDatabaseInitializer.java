package com.example.twogisads;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

// конфигурация для тестов: создаёт таблицы в in-memory h2 перед выполнением тестов
@TestConfiguration
public class TestDatabaseInitializer {
    @Bean
    public Object initializeTestDatabase(DataSource dataSource) {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("create table if not exists clients (client_id int auto_increment primary key, legal_name varchar(255), company_name varchar(255), contact_person varchar(255), phone varchar(50), email varchar(255))");
            st.executeUpdate("create table if not exists ad_packages (package_id int auto_increment primary key, package_name varchar(255), description varchar(1000), monthly_fee decimal(19,2))");
            st.executeUpdate("create table if not exists placements (placement_id int auto_increment primary key, client_id int, package_id int, start_date date, end_date date, monthly_payment decimal(19,2))");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Object();
    }
}
