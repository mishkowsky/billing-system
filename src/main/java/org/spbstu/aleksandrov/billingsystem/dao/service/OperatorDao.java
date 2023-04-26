package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Operator;

import java.util.List;

public interface OperatorDao {

    Operator addOperator(Operator operator);
    void delete(int id);
    Operator editOperator(Operator operator);
    List<Operator> getAll();

    Operator findByName(String name);
}
