package org.spbstu.aleksandrov.billingsystem.crm;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Operator;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Tariff;
import org.spbstu.aleksandrov.billingsystem.dao.service.CustomerDao;
import org.spbstu.aleksandrov.billingsystem.dao.service.OperatorDao;
import org.spbstu.aleksandrov.billingsystem.dao.service.TariffDao;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final CustomerDao customerDao;
    private final TariffDao tariffDao;
    private final OperatorDao operatorDao;
    private final AtomicInteger changeTariffCounter = new AtomicInteger();
    private final MessageSenderCrm sender;
    private boolean crmDone = false;

    public ManagerController(CustomerDao customerDao, TariffDao tariffDao, OperatorDao operatorDao, MessageSenderCrm sender) {
        this.customerDao = customerDao;
        this.tariffDao = tariffDao;
        this.operatorDao = operatorDao;
        this.sender = sender;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, path = "/changeTariff/")
    public ChangeTariffResponse changeTariff(
            @RequestParam(value = "numberPhone") Long phone,
            @RequestParam(value = "tariff_id") String tariffCode
    ) {
        Tariff tariff = tariffDao.findByTariffCode(tariffCode);
        Customer customer = customerDao.getCustomerByNumber(phone);
        customer.setTariff(tariff);
        customer.setMinutesLeft(tariff.getMinutesLimit());
        customerDao.editCustomer(customer);
        return new ChangeTariffResponse(changeTariffCounter.incrementAndGet(), phone, tariffCode);
    }

    @Getter
    public static class ChangeTariffResponse implements Serializable {
        private final int id;
        private final long numberPhone;
        private final String tariffCode;

        public ChangeTariffResponse(int id, long numberPhone, String tariffCode) {
            this.id = id;
            this.numberPhone = numberPhone;
            this.tariffCode = tariffCode;
        }
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, path = "/abonent/")
    public AddAbonentResponse addAbonentResponse(
            @RequestParam(value = "numberPhone") Long phone,
            @RequestParam(value = "tariff_id") String tariffCode,
            @RequestParam(value = "tariff_id") int balance // в копейках
    ) {
        Tariff tariff = tariffDao.findByTariffCode(tariffCode);
        Operator operator = operatorDao.findByName("Ромашка");
        Customer customer = new Customer(
                phone, tariff, operator, balance, tariff.getMinutesLimit(), new Date(), ""
        );
        customerDao.editCustomer(customer);
        return new AddAbonentResponse(customer.getPhone(), tariff.getCode(), customer.getBalance());
    }

    @Getter
    public static class AddAbonentResponse implements Serializable {
        private final long numberphone;
        private final String tariff_id;
        private final int balance;

        public AddAbonentResponse(long numberphone, String tariffId, int balance) {
            this.numberphone = numberphone;
            tariff_id = tariffId;
            this.balance = balance;
        }
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, path = "/billing/")
    public BillingResponse billing() throws InterruptedException {
        Date now = new Date();
        crmDone = false;
        sender.sendMessage();
        log.info("MESSAGE SEND WAIT FOR RESPONSE RESPONSE");
        // TODO perform asynchronous wait??
        while (!crmDone) {
            log.info("WAITING");
            Thread.sleep(500);
            continue;
        }
        log.info("CRM RECEIVED RESPONSE");
        List<Customer> customers = customerDao.findByGreaterDate(now);
        Collection<NumberResponse> numberResponses = new ArrayList<>();
        for (Customer customer : customers) {
            numberResponses.add(new NumberResponse(customer.getPhone(), customer.getBalance()));
        }
        crmDone = false;
        return new BillingResponse(numberResponses);
    }

    public static class BillingResponse implements Serializable {
        private final Collection<NumberResponse> numbers;

        public BillingResponse(Collection<NumberResponse> numbers) {
            this.numbers = numbers;
        }
    }

    private static class NumberResponse implements Serializable {
        private final long phoneNumber;
        private final int balance;

        private NumberResponse(long phoneNumber, int balance) {
            this.phoneNumber = phoneNumber;
            this.balance = balance;
        }
    }

    public void setCrmDone(boolean newValue) {
        crmDone = newValue;
    }

}
