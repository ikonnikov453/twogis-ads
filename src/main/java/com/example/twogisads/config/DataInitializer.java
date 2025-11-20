package com.example.twogisads.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

// класс инициализации базы данных при старте приложения
@Component
public class DataInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection c = dataSource.getConnection()) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("create table if not exists clients (client_id int auto_increment primary key, legal_name varchar(255), company_name varchar(255), contact_person varchar(255), phone varchar(50), email varchar(255))");
                st.executeUpdate("create table if not exists ad_packages (package_id int auto_increment primary key, package_name varchar(255), description varchar(1000), monthly_fee decimal(19,2))");
                st.executeUpdate("create table if not exists placements (placement_id int auto_increment primary key, client_id int, package_id int, start_date date, end_date date, monthly_payment decimal(19,2), constraint fk_client foreign key (client_id) references clients(client_id), constraint fk_package foreign key (package_id) references ad_packages(package_id))");
            }

            try (PreparedStatement ps = c.prepareStatement("select count(*) from clients");
                 ResultSet rs = ps.executeQuery()) {
                boolean empty = true;
                if (rs.next()) empty = rs.getInt(1) == 0;
                if (empty) {
                    try (PreparedStatement ins = c.prepareStatement("insert into clients(legal_name, company_name, contact_person, phone, email) values (?,?,?,?,?)")) {
                        ins.setString(1, "ООО рога и копыта");
                        ins.setString(2, "рога");
                        ins.setString(3, "иван иванов");
                        ins.setString(4, "+7 900 000 00 01");
                        ins.setString(5, "ivan@example.com");
                        ins.executeUpdate();

                        ins.setString(1, "ooo пример");
                        ins.setString(2, "пример фирма");
                        ins.setString(3, "петр петров");
                        ins.setString(4, "+7 900 000 00 02");
                        ins.setString(5, "petr@example.com");
                        ins.executeUpdate();
                    }

                    try (PreparedStatement ins = c.prepareStatement("insert into ad_packages(package_name, description, monthly_fee) values (?,?,?)")) {
                        ins.setString(1, "Старт медиа");
                        ins.setString(2, "базовый пакет для старта");
                        ins.setBigDecimal(3, new BigDecimal("5000.00"));
                        ins.executeUpdate();

                        ins.setString(1, "Стандарт медиа");
                        ins.setString(2, "популярный пакет");
                        ins.setBigDecimal(3, new BigDecimal("12000.00"));
                        ins.executeUpdate();

                        ins.setString(1, "Всё включено");
                        ins.setString(2, "премиум пакет со всеми опциями");
                        ins.setBigDecimal(3, new BigDecimal("30000.00"));
                        ins.executeUpdate();
                    }

                    try (PreparedStatement ins = c.prepareStatement("insert into placements(client_id, package_id, start_date, end_date, monthly_payment) values (?,?,?,?,?)")) {
                        ins.setInt(1, 1);
                        ins.setInt(2, 1);
                        ins.setDate(3, java.sql.Date.valueOf(LocalDate.now().minusMonths(2)));
                        ins.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusMonths(10)));
                        ins.setBigDecimal(5, new BigDecimal("5500.00"));
                        ins.executeUpdate();

                        ins.setInt(1, 2);
                        ins.setInt(2, 2);
                        ins.setDate(3, java.sql.Date.valueOf(LocalDate.now().minusMonths(1)));
                        ins.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusMonths(5)));
                        ins.setBigDecimal(5, new BigDecimal("12500.00"));
                        ins.executeUpdate();
                    }
                }
            }
        }
    }
}
