package org.spbstu.aleksandrov.billingsystem.brt;


import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.service.CallDao;
import org.spbstu.aleksandrov.billingsystem.dao.service.CustomerDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Brt {

    private final CallDao callDao;
    private final CustomerDao customerDao;
    private final String cdrPlusFilePath;
    private final MessageSenderBrt sender;

    public Brt(CallDao callDao, CustomerDao customerDao, @Value("${cdr.plus.path}") String cdrPlusFilePath, MessageSenderBrt sender) {
        this.callDao = callDao;
        this.customerDao = customerDao;
        this.cdrPlusFilePath = cdrPlusFilePath;
        this.sender = sender;
    }

    public void processCdr(File file) {
        try {
            File cdrFile = new File(cdrPlusFilePath);
            cdrFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(cdrFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line = reader.readLine();

            while (line != null) {
                String patternString = "(?<=\\d,)\\d+";

                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(line);
                long number;
                if (matcher.find())
                    number = Long.parseLong(matcher.group(0));
                else throw new IllegalStateException();

                Customer customer = customerDao.getCustomerByNumber(number);

                String cdrPlusLine = customer.getTariff().getCode() + "," + line + "\n";

                if (customer.getBalance() - customer.getTariff().getPrice() > 0
                        && customer.getOperator().getName().equals("Ромашка"))
                    fos.write(cdrPlusLine.getBytes());

                line = reader.readLine();
            }
            fileReader.close();
            reader.close();
            fos.close();
            sender.sendMessage(cdrFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateBalances(Map<Customer, Integer> callsCosts) {
        for (Map.Entry<Customer, Integer> entry : callsCosts.entrySet()) {
            Customer customer = entry.getKey();
            customer.setBalance(customer.getBalance() - entry.getValue() - customer.getTariff().getPrice());
            customerDao.editCustomer(customer);
            System.out.println(callDao.getAll().size());
        }
    }
}
