package org.spbstu.aleksandrov.billingsystem.cdr;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageListenerCdr {

    public final static int CALLS_PER_CUSTOMER = 3;

    private final CdrGenerator cdr;

    public MessageListenerCdr(CdrGenerator cdr) {
        this.cdr = cdr;
    }

    @JmsListener(destination = "cdr-generate")
    public void generateCDR() {
        try {
            cdr.generateCdr(CALLS_PER_CUSTOMER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
