package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Tariff;
import org.spbstu.aleksandrov.billingsystem.dao.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TariffDaoImpl implements TariffDao{

    @Autowired
    private TariffRepository tariffRepository;

    @Override
    public Tariff addTariff(Tariff tariff) {
        return tariffRepository.saveAndFlush(tariff);
    }

    @Override
    public void delete(int id) {
        tariffRepository.deleteById(id);
    }

    @Override
    public Tariff editTariff(Tariff tariff) {
        return tariffRepository.saveAndFlush(tariff);
    }

    @Override
    public List<Tariff> getAll() {
        return tariffRepository.findAll();
    }

    @Override
    public Tariff findByTariffCode(String tariffCode) {
        return tariffRepository.findByCode(tariffCode);
    }
}
