package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Call;

import java.util.List;

public interface CallDao {

    Call addCall(Call call);
    void delete(int id);
    Call editCall(Call call);
    List<Call> getAll();

}
