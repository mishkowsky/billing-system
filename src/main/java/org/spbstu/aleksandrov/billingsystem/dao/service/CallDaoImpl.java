package org.spbstu.aleksandrov.billingsystem.dao.service;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Call;
import org.spbstu.aleksandrov.billingsystem.dao.repository.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CallDaoImpl implements CallDao {

    @Autowired
    CallRepository callRepository;

    @Override
    public Call addCall(Call call) {
        return callRepository.saveAndFlush(call);
    }

    @Override
    public void delete(int id) {
        callRepository.deleteById(id);
    }

    @Override
    public Call editCall(Call call) {
        return callRepository.saveAndFlush(call);
    }

    @Override
    public List<Call> getAll() {
        return callRepository.findAll();
    }
}
