package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Operator;
import org.spbstu.aleksandrov.billingsystem.dao.repository.OperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorDaoImpl implements OperatorDao {

    @Autowired
    OperatorRepository operatorRepository;

    @Override
    public Operator addOperator(Operator operator) {
        return operatorRepository.saveAndFlush(operator);
    }

    @Override
    public void delete(int id) {
        operatorRepository.deleteById(id);
    }

    @Override
    public Operator editOperator(Operator operator) {
        return operatorRepository.saveAndFlush(operator);
    }

    @Override
    public List<Operator> getAll() {
        return operatorRepository.findAll();
    }

    @Override
    public Operator findByName(String name) {
        return operatorRepository.findByName(name);
    }
}
