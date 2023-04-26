package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Price;
import org.spbstu.aleksandrov.billingsystem.dao.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceDaoImpl implements PriceDao {

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public Price addPrice(Price price) {
        return priceRepository.saveAndFlush(price);
    }

    @Override
    public void delete(int id) {
        priceRepository.deleteById(id);
    }

    @Override
    public Price editPrice(Price price) {
        return priceRepository.saveAndFlush(price);
    }

    @Override
    public List<Price> getAll() {
        return priceRepository.findAll();
    }

    @Override
    public Price findByTariffIdAndOperatorIdAndCallTypeAndPriceType(
            int tariffId, int operatorId, Price.CallType callType, Price.PriceType priceType
    ) {
        return priceRepository.findByTariffIdAndOperatorIdAndCallTypeAndPriceType(
                tariffId, operatorId, callType, priceType
        );
    }
}
