package org.spbstu.aleksandrov.billingsystem.crm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

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
        template.convertAndSend(destination, "");
    }
}