package org.spbstu.aleksandrov.billingsystem.cdr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MessageListenerCdr {

    public final static int CALLS_PER_CUSTOMER = 3;

    private final CDRGenerator cdr;

    public MessageListenerCdr(CDRGenerator cdr) {
        this.cdr = cdr;
    }

    @JmsListener(destination = "cdr-generate")
    public void generateCDR() {
        try {
            log.info("START CDR GENERATION");
            cdr.generateCDR(CALLS_PER_CUSTOMER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
