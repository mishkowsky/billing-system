package org.spbstu.aleksandrov.billingsystem.crm;

import lombok.Getter;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Call;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.service.CustomerDao;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/abonent")
public class CustomerController {

    private final AtomicInteger payCounter = new AtomicInteger();
    private final AtomicInteger reportCounter = new AtomicInteger();
    private final CustomerDao customerDao;
    Pattern rublesPattern = Pattern.compile("^\\d+");
    Pattern kopeckPattern = Pattern.compile("\\d{2}(?<=\\d.)");

    public CustomerController(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/pay")
    public PayResponse pay(
            @RequestParam(value = "numberPhone") Long phone,
            @RequestParam(value = "money") String money
    ) {
        Customer customer = customerDao.getCustomerByNumber(phone);

        if (!money.matches("\\d+.\\d{2}")) throw new IllegalArgumentException();

        int rubles;
        Matcher rublesMatcher = rublesPattern.matcher(money);
        if (rublesMatcher.find()) rubles = Integer.parseInt(rublesMatcher.group(0));
        else throw new IllegalArgumentException();

        int kopeck;
        Matcher kopeckMatcher = kopeckPattern.matcher(money);
        if (kopeckMatcher.find()) kopeck = Integer.parseInt(kopeckMatcher.group(0));
        else throw new IllegalArgumentException();

        customer.setBalance(customer.getBalance() + rubles * 100 + kopeck);
        customerDao.editCustomer(customer);
        return new PayResponse(payCounter.incrementAndGet(), phone, rubles * 100 + kopeck);
    }

    @Getter
    public static class PayResponse {
        private final int id;
        private final long numberPhone;
        private final String money; // В рублях (х.хх)

        public PayResponse(int id, long numberPhone, int money) {
            this.id = id;
            this.numberPhone = numberPhone;
            this.money = money / 100 + "." + String.format("%02d", money % 100); // в рублях (х.хх)
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/report/{numberPhone}")
    public ReportResponse report(@PathVariable("numberPhone") long phone, @AuthenticationPrincipal User user)
            throws IllegalAccessException {

        if (user.getPhoneNumber() != phone) throw new IllegalAccessException();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("HH:mm:ss");

        Customer customer = customerDao.getCustomerByNumber(phone);
        int totalCost = customer.getTariff().getPrice();

        List<Call> calls = customer.getIncomingCalls();
        calls.addAll(customer.getOutgoingCalls());
        calls.sort(Comparator.comparing(Call::getId));

        Collection<CallResponse> callsResponses = new ArrayList<>();
        for (Call call : calls) {
            // TODO
            String callType;
            if (call.getCaller() == null) callType = "02"; // INCOMING
            else callType = "01"; // OUTGOING

            totalCost += call.getPrice();

            callsResponses.add(new CallResponse(
                    callType,
                    simpleDateFormat.format(call.getStartDate()),
                    simpleDateFormat.format(call.getEndDate()),
                    minutesFormat.format(call.getEndDate().getTime() - call.getStartDate().getTime() - 3 * 3600000),
                    call.getPrice() / 100 + "." + String.format("%02d", call.getPrice() % 100)
            ));
        }

        return new ReportResponse(
                reportCounter.incrementAndGet(), phone, customer.getTariff().getCode(), callsResponses,
                totalCost / 100 + "." + String.format("%02d", totalCost % 100), "rubles"
        );
    }

    @Getter
    public static class ReportResponse implements Serializable {
        private final int id;
        private final long numberPhone;
        private final String tariffCode;
        private final Collection<CallResponse> payload;
        private final String totalCost; // в рублях (х.хх)
        private final String monetaryUnit;

        public ReportResponse(
                int id, long numberPhone, String tariffCode, Collection<CallResponse> payload,
                String totalCost, String monetaryUnit) {
            this.id = id;
            this.numberPhone = numberPhone;
            this.tariffCode = tariffCode;
            this.payload = payload;
            this.totalCost = totalCost;
            this.monetaryUnit = monetaryUnit;
        }
    }

    @Getter
    public static class CallResponse implements Serializable {
        private final String callType;
        private final String startTime;
        private final String endTime;
        private final String duration;
        private final String cost;

        public CallResponse(String callType, String startTime, String endTime, String duration, String cost) {
            this.callType = callType;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.cost = cost;
        }
    }


}