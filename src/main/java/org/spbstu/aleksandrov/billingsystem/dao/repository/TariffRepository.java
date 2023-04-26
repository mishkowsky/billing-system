package org.spbstu.aleksandrov.billingsystem.dao.repository;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {
    Tariff findByCode(String tariffCode);
}
