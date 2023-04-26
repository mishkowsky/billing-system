package org.spbstu.aleksandrov.billingsystem.dao.repository;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    Operator findByName(String name);
}
