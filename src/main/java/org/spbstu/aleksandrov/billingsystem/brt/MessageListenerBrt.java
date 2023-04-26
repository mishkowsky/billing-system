package org.spbstu.aleksandrov.billingsystem.brt;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.spbstu.aleksandrov.billingsystem.dao.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

@Service
public class MessageListenerBrt {

    private final Brt brt;
    private final String pathCdrReceived;
    private final MessageConverter messageConverter;

    public MessageListenerBrt(Brt brt, @Value("${path.cdr.received}") String pathCdrReceived) {
        this.brt = brt;
        this.pathCdrReceived = pathCdrReceived;
        this.messageConverter = new SimpleMessageConverter();
    }

    @JmsListener(destination = "cdr-file")
    public void processCdrFile(@Payload Message message) {
        try {
            if (message instanceof BytesMessage bytesMessage) {
                int length = (int) bytesMessage.getBodyLength();
                byte[] bytes = new byte[length];
                bytesMessage.readBytes(bytes, length);
                File cdrPlusFile = new File(pathCdrReceived);
                cdrPlusFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(cdrPlusFile);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                brt.processCdr(cdrPlusFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @JmsListener(destination = "calls-costs")
    public void updateBalances(@Payload Message message) {
        System.out.println("Received smth from queue");
        Map<Customer, Integer> map = null;
        try {
            map = (Map<Customer, Integer>) messageConverter.fromMessage(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        brt.updateBalances(map);
    }

}
