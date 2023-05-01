package org.spbstu.aleksandrov.billingsystem.brt;

import jakarta.jms.BytesMessage;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class MessageListenerBrt {

    private final Brt brt;
    private final String pathCdrReceived;

    public MessageListenerBrt(Brt brt, @Value("${path.cdr.received}") String pathCdrReceived) {
        this.brt = brt;
        this.pathCdrReceived = pathCdrReceived;
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
}
