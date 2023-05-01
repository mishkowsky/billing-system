package org.spbstu.aleksandrov.billingsystem;

import org.spbstu.aleksandrov.billingsystem.cdr.CdrGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class BillingSystemApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(BillingSystemApplication.class, args);
        context.getBean(CdrGenerator.class).generateCdr(3);
    }
}
