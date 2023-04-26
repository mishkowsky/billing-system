package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Price;

import java.util.List;

public interface PriceDao {

    Price addPrice(Price price);
    void delete(int id);
    Price editPrice(Price price);
    List<Price> getAll();

    Price findByTariffIdAndOperatorIdAndCallTypeAndPriceType(
            int tariffId, int operatorId, Price.CallType callType, Price.PriceType priceType);

}
