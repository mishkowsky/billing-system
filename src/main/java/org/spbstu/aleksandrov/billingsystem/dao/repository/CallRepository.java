package org.spbstu.aleksandrov.billingsystem.dao.repository;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallRepository extends JpaRepository<Call, Integer> {
}