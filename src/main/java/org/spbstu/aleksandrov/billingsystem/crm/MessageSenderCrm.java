package org.spbstu.aleksandrov.billingsystem.crm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageSenderCrm {

    private final JmsTemplate template;
    private final String destination;

    public MessageSenderCrm(JmsTemplate template,
                            @Value("${cdr.generate}") String destination) {
        this.template = template;
        this.destination = destination;
    }

    public void sendMessage() {
        log.info("MESSAGE SEND TO CDR GENERATE");
        template.convertAndSend(destination, "");
    }


}