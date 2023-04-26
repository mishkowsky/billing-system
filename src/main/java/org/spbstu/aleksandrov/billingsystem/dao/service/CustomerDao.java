package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    Customer addCustomer(Customer customer);

    void delete(int id);

    Customer editCustomer(Customer customer);

    List<Customer> getAll();

    Customer getCustomer(Integer id);

    Customer getCustomerByNumber(Long number);

    List<Customer> findByGreaterDate(Date date);

}
