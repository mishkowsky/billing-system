package org.spbstu.aleksandrov.billingsystem.dao.repository;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Integer> {
    Price findByTariffIdAndOperatorIdAndCallTypeAndPriceType(int idTariff, int idOperator,
            Price.CallType callType, Price.PriceType priceType
    );

}
