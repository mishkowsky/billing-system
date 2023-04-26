package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Tariff;

import java.util.List;

public interface TariffDao {

    Tariff addTariff(Tariff tariff);
    void delete(int id);
    Tariff editTariff(Tariff tariff);
    List<Tariff> getAll();

    Tariff findByTariffCode(String tariffCode);

}
