package org.spbstu.aleksandrov.billingsystem.dao.service;

import jakarta.persistence.EntityNotFoundException;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerDaoImpl implements CustomerDao {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public void delete(int id) {
        customerRepository.deleteById(id);
    }

    @Override
    public Customer editCustomer(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) return optionalCustomer.get();
        else throw new EntityNotFoundException();
    }

    @Override
    public Customer getCustomerByNumber(Long number) {
        return customerRepository.findByPhone(number);
    }

    @Override
    public List<Customer> findByGreaterDate(Date date) {
        return customerRepository.findByGreaterDate(date);
    }
}