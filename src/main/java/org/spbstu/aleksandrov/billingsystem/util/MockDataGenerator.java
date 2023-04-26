package org.spbstu.aleksandrov.billingsystem.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MockDataGenerator {

    private static final Random rand = new Random();

    public static void main(String[] args) {
        try {
            generateMockData("./src/main/resources/mock.sql");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateLine() {

        int minutes_left = rand.nextInt(0, 301);
        if (rand.nextInt(0, 100) < 75) {
            if (rand.nextInt(0, 100) < 40) minutes_left %= 100;
            else minutes_left = 0;
        }

        // Цены в копейках balance = [-10rub; 200rub]
        int balance = rand.nextInt(-1000, 20001);

        // Распределение вероятностей "на глазок",
        // чтобы равномерно распределить типы тарифов
        int tariffId;
        if (minutes_left == 0) {
            if (rand.nextBoolean()) tariffId = 3;
            else if (rand.nextBoolean()) tariffId = 11;
            else tariffId = 6;
        } else if (minutes_left > 100) tariffId = 6;
        else if (rand.nextInt(0, 100) > 75) tariffId = 6;
        else tariffId = 11;

        String operatorName = "Ромашка";
        if (rand.nextInt(0, 100) > 70) operatorName = "Одуванчик";

        return String.format(
                ",select id from tariff where tariff_code='%02d'," +
                        "select id from operator where operator_name='%s',%d,%d",
                tariffId, operatorName, balance, minutes_left
        );
    }

    public static Set<String> generatePhoneNumbers(long n) {
        Set<String> phoneNumbers = new HashSet<>();
        for (int i = 0; i < n; i++) {
            String phone = "7" + String.format("%010d", rand.nextLong(0, 10000000000L));
            while (phoneNumbers.contains(phone))
                phone = "7" + String.format("%010d", rand.nextLong(0, 10000000000L));
            phoneNumbers.add(phone);
        }
        return phoneNumbers;
    }

    public static void generateMockData(String outputPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputPath);
        fos.write("phone_number,id_tariff,id_operator,balance,remain_minutes\n".getBytes());
        Set<String> phoneNumbers = generatePhoneNumbers(5000);
        for (String phone : phoneNumbers) {
            String toWrite =
                    "insert into customer (phone_number, id_tariff, id_operator, balance, remain_minutes) values (" +
                            phone + generateLine() + ");\n";
            fos.write(toWrite.getBytes());
        }
    }
}
