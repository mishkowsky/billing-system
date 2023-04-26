package org.spbstu.aleksandrov.billingsystem.hrs;

import jakarta.jms.BytesMessage;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class MessageListener {

    private final Hrs hrs;
    private final String pathCdrPlusReceived;

    public MessageListener(Hrs hrs, @Value("${path.cdr.plus.received}") String pathCdrPlusReceived) {
        this.hrs = hrs;
        this.pathCdrPlusReceived = pathCdrPlusReceived;
    }

    @JmsListener(destination = "cdr-plus-file")
    public void processCdrPlusFile(@Payload Message message) {
        try {
            if (message instanceof BytesMessage bytesMessage) {
                int length = (int) bytesMessage.getBodyLength();
                byte[] bytes = new byte[length];
                bytesMessage.readBytes(bytes, length);
                File cdrPlusFile = new File(pathCdrPlusReceived);
                cdrPlusFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(cdrPlusFile);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                hrs.parseCdrPlus(cdrPlusFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
