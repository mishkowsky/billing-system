package org.spbstu.aleksandrov.billingsystem.hrs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderHrs {

    private final JmsTemplate template;
    private final String destination;

    public MessageSenderHrs(JmsTemplate template,
                            @Value("${hrs.done}") String destination) {
        this.template = template;
        this.destination = destination;
    }

    public void sendMessage() {
        template.convertAndSend(destination, "");
    }

}
