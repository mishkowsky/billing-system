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
public class CdrGenerator {

    private final CustomerDao customerDao;
    private final MessageSenderCdr messageSenderCdr;
    // Используются для генерации дат в следующем периоде (месяце) при повторном вызове generateCDR()
    private int methodCallCounter = 0;
    private final static Random rand = new Random();
    private final String cdrFilePath;
    private Calendar startPeriodDate;
    private Calendar endPeriodDate;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    public CdrGenerator(CustomerDao customerDao, MessageSenderCdr messageSenderCdr,
                        @Value("${cdr.path}") String cdrFilePath) {
        this.customerDao = customerDao;
        this.messageSenderCdr = messageSenderCdr;
        this.cdrFilePath = cdrFilePath;
    }

    public void generateCdr(int callsPerCustomer) throws IOException {

        Set<Long> numbers = new HashSet<>();
        List<Customer> customers = customerDao.getAll();
        customers.forEach(c -> numbers.add(c.getPhone()));

        startPeriodDate = Calendar.getInstance();
        startPeriodDate.add(Calendar.MONTH, methodCallCounter - 1);

        endPeriodDate = Calendar.getInstance();
        endPeriodDate.add(Calendar.MONTH, methodCallCounter);

        File cdrFile = new File(cdrFilePath);
        cdrFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(cdrFile);

        Random rand = new Random();

        // Для каждого номера генерируем [1; 2 * callsPerCustomer] звонков,
        // в среднем выходит callsPerCustomer на абонента
        for (Long number : numbers) {
            int n = rand.nextInt(1, 2 * callsPerCustomer);
            while (n > 0) {
                String line = generateCdrLine(number);
                fos.write(line.getBytes());
                n--;
            }
        }
        methodCallCounter++;
        fos.close();
        messageSenderCdr.sendMessage(cdrFile.getPath());
    }

    public Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = rand.nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }

    public String generateCdrLine(Long number) {
        int callType = rand.nextInt(1, 3);
        Date startDate = between(
                startPeriodDate.getTime(),
                endPeriodDate.getTime()
        );

        // Для генерации продолжительности звонков возьмем экспоненциальный закон распределния,
        // т.к. вероятность короткого звонка гораздо выше, чем продолжительного
        // Функция экспоненциального распределения F(x) = 1 - e^(-λx)
        // Пусть вероятность звонка длительностью 300 минут (300*60*1000 миллисекунд) 1%, т.е. F(300*60*1000) = 0.99
        // 1 - e^(λ*300*60*1000) = 0.99, откуда λ = 2.56e-7
        // Сгенерировать случайную велечину, подчиняющуюся закону экспоненциального распредления,
        // используя генератор равномерного распредления можно как
        // log(1-uniformRand(0, 1))/(-λ)
        long duration = (long) (Math.log(1-rand.nextDouble())/(-2.56e-7f));
        Date endDate = new Date(startDate.getTime() + duration);

        return String.format(
                "%02d,%11d,%s,%s\n",
                callType, number, dateFormat.format(startDate), dateFormat.format(endDate)
        );
    }
}
