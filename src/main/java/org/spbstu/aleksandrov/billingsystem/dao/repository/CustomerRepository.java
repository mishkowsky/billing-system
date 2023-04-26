package org.spbstu.aleksandrov.billingsystem.dao.repository;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("select b from Customer b where b.phone = :phone")
    Customer findByPhone(@Param("phone") Long phone);

    @Query("select b from Customer b where b.updateTime > :date")
    List<Customer> findByGreaterDate(@Param("date") Date date);
}