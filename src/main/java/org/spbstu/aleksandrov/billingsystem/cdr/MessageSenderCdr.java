package org.spbstu.aleksandrov.billingsystem.cdr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import jakarta.jms.*;

import java.io.FileInputStream;

@Service
public class MessageSenderCdr {

    private final JmsTemplate template;
    private final String destination;

    public MessageSenderCdr(JmsTemplate template,
                            @Value("${cdr.file}") String destination) {
        this.template = template;
        this.destination = destination;
    }

    public void sendMessage(String fileName) {
        template.send(destination, session -> {
            try {
                BytesMessage bytesMessage = session.createBytesMessage();
                FileInputStream fileInputStream = new FileInputStream(fileName);
                final int BUFLEN = 64;
                byte[] buf1 = new byte[BUFLEN];
                int bytes_read;
                while ((bytes_read = fileInputStream.read(buf1)) != -1) {
                    bytesMessage.writeBytes(buf1, 0, bytes_read);
                }
                fileInputStream.close();
                return bytesMessage;
            } catch (Exception e) {
                return null;
            }
        });
    }
}
