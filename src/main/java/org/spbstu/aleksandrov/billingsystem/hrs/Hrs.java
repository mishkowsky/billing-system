package org.spbstu.aleksandrov.billingsystem.hrs;

import lombok.extern.slf4j.Slf4j;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Call;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Price;
import org.spbstu.aleksandrov.billingsystem.dao.service.CallDao;
import org.spbstu.aleksandrov.billingsystem.dao.service.CustomerDao;
import org.spbstu.aleksandrov.billingsystem.dao.service.PriceDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Hrs {

    private final CustomerDao customerDao;
    private final PriceDao priceDao;
    private final MessageSenderHrs messageSenderHrs;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public Hrs(CustomerDao customerDao, PriceDao priceDao, MessageSenderHrs messageSenderHrs) {
        this.customerDao = customerDao;
        this.priceDao = priceDao;
        this.messageSenderHrs = messageSenderHrs;
    }

    // HRS не возвращает ничего в BRT, а сам пишет в БД обновленные после тарификации данные.
    // HRS по ТЗ должен был заниматься только расчетом стоимости всех звонков всех абонентов на основе CDR+ и не
    // обращаться в БД.
    // В таком случае CDR+ должен был бы содержать всю информацию об абоненте (баланс, остаток по минутам, оператора)
    // и его тарифе (цены для всех возможных случаев).
    // В противном случае, если CDR+ этого не содержит, то ресчет тарифа задается hard кодом в HRS.
    // Кроме того, HRS должен возвращать не только новый баланс, но и список всех
    // звонков абонента (иначе откуда они берутся СRM'ом, как не из БД, в которую пишет BRT), остаток по минутам.
    // Поэтому для возможной более гибкой настройки тарифов и упрощения обмена данными между BRT и HRS,
    // 1. Все данные о тарифе хранятся в БД (https://dbdiagram.io/d/6442ebf96b31947051ff99af)
    // в таблицах tariff и price_per_minute
    // 2. HRS имеет доступ к БД.
    // И так как HRS уже завязан с БД, то нету смысла пересылать результаты в BRT
    // и там производить запись, если можно записать в БД здесь же в HRS.
    public void parseCdrPlus(File file) {
        try {
            Map<Customer, List<Call>> customers = new HashMap<>();
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line = reader.readLine();

            while (line != null) {

                String patternString = "\\d+";

                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(line);

                Price.CallType callType;
                Customer customer;
                Date beginDate;
                Date endDate;
                int tariffId;

                if (matcher.find())
                    tariffId = Integer.parseInt(matcher.group());
                else throw new IllegalStateException();

                if (matcher.find())
                    callType = matcher.group().equals("01") ? Price.CallType.OUTGOING : Price.CallType.INCOMING;
                else throw new IllegalStateException();

                if (matcher.find())
                    customer = customerDao.getCustomerByNumber(Long.parseLong(matcher.group()));
                else throw new IllegalStateException();

                if (matcher.find())
                    beginDate = dateFormat.parse(matcher.group());
                else throw new IllegalStateException();

                if (matcher.find())
                    endDate = dateFormat.parse(matcher.group());
                else throw new IllegalStateException();

                List<Call> calls = customers.computeIfAbsent(customer, k -> new ArrayList<>());

                if (callType == Price.CallType.INCOMING)
                    calls.add(new Call(null, customer, 0, beginDate, endDate, ""));
                else
                    calls.add(new Call(customer, null, 0, beginDate, endDate, ""));

                line = reader.readLine();
            }
            reader.close();
            fileReader.close();
            processCalls(customers);
            messageSenderHrs.sendMessage();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void processCalls(Map<Customer, List<Call>> customers) {
        Map<Customer, Integer> totalCallsCosts = new HashMap<>();
        customerLoop:
        for (Customer customer : customers.keySet()) {
            int totalCost = customer.getTariff().getPrice();
            // TODO вычитать абонентскую плату в начале или конце (в момент тарификации) периода ?
            int balance = customer.getBalance() - customer.getTariff().getPrice();
            List<Call> calls = customers.get(customer);
            calls.sort(Comparator.comparing(Call::getStartDate));
            for (Call call : calls) {

                // TODO
                Price.CallType callType;
                if (call.getCaller() == null) callType = Price.CallType.INCOMING;
                else callType = Price.CallType.OUTGOING;
//                Price.CallType callType = call.getCallee().getId() == customer.getId() ?
//                        Price.CallType.INCOMING : Price.CallType.OUTGOING;

                Price.PriceType priceType = customer.getMinutesLeft() > 0 ?
                        Price.PriceType.BELOW_LIMIT : Price.PriceType.OVER_LIMIT;

                int pricePerMinute = priceDao.findByTariffIdAndOperatorIdAndCallTypeAndPriceType(
                        customer.getTariff().getId(), customer.getOperator().getId(), callType, priceType).getPrice();

                // Получаем минуты с округленеим вверх (+1, если остаток > 0 )
                long timeDelta = call.getEndDate().getTime() - call.getStartDate().getTime();
                int duration = (int) (timeDelta / 60000) + timeDelta % 60000 > 0 ? 1 : 0;
                int callCost;
                if (customer.getMinutesLeft() - duration > 0) {
                    callCost = 0;
                    customer.setMinutesLeft(customer.getMinutesLeft() - duration);
                } else {
                    callCost = (duration - customer.getMinutesLeft()) * pricePerMinute;
                    customer.setMinutesLeft(0);
                }

                // Добавляем звонок в список звонков абонента для внесения в БД
                if (callType == Price.CallType.INCOMING) customer.getIncomingCalls().add(call);
                else customer.getOutgoingCalls().add(call);

                balance -= callCost;
                // Если у абонента счет ушел в минус, то считаем, что все последующие звонки не были совершены
                if (balance < 0) continue customerLoop;
                call.setPrice(callCost);
                totalCost += callCost;
            }
            customer.setBalance(balance);
            customer.setUpdateTime(new Date());
            customerDao.editCustomer(customer);
        }
    }
}
