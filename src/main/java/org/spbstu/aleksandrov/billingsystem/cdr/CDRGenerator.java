package org.spbstu.aleksandrov.billingsystem.cdr;

import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.spbstu.aleksandrov.billingsystem.dao.service.CustomerDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CDRGenerator {

    private final CustomerDao customerDao;
    private final MessageSenderCdr messageSenderCdr;
    // Используются для генерации дат в следующем периоде (месяце) при повторном вызове generateCDR()
    private int callCounter = 0;
    private final static Random rand = new Random();
    private final String cdrFilePath;

    public CDRGenerator(CustomerDao customerDao, MessageSenderCdr messageSenderCdr,
                        @Value("${cdr.path}") String cdrFilePath) {
        this.customerDao = customerDao;
        this.messageSenderCdr = messageSenderCdr;
        this.cdrFilePath = cdrFilePath;
    }

    public void generateCDR(int callsPerCustomer) throws IOException {

        Set<Long> numbers = new HashSet<>();
        List<Customer> customers = customerDao.getAll();
        customers.forEach(c -> numbers.add(c.getPhone()));

        File cdrFile = new File(cdrFilePath);
        cdrFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(cdrFile);

        Random rand = new Random();

        for (Long number : numbers) {
            int n = rand.nextInt(1, 2 * callsPerCustomer);
            while (n > 0) {
                String line = generateCDRLine(number);
                fos.write(line.getBytes());
                n--;
            }
        }
        callCounter++;
        fos.close();
        messageSenderCdr.sendMessage(cdrFile.getPath());
    }

    public Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();

        long randomMillisSinceEpoch = rand.nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }

    public String generateCDRLine(Long number) {
        int callType = rand.nextInt(1, 3);

        Calendar startPeriodDate = Calendar.getInstance();
        startPeriodDate.add(Calendar.MONTH, -1);

        Calendar endPeriodDate = Calendar.getInstance();
        startPeriodDate.add(Calendar.MONTH, 0);

        Date startDate = between(
                startPeriodDate.getTime(),
                endPeriodDate.getTime()
        );

        long callDuration = rand.nextLong(1, 18000001);
        Date endDate = new Date(startDate.getTime() + callDuration);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH24mmss");
        return String.format(
                "%02d,%11d,%s,%s\n",
                callType, number, dateFormat.format(startDate), dateFormat.format(endDate)
        );
    }
}
