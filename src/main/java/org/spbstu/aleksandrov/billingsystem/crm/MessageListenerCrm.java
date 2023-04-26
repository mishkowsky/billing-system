package org.spbstu.aleksandrov.billingsystem.crm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageListenerCrm {

    private final ManagerController managerController;

    public MessageListenerCrm(ManagerController managerController) {
        this.managerController = managerController;
    }

    @JmsListener(destination = "hrs-done")
    public void setCrmDoneFlag() {
        log.info("CRM RECEIVED SETTING FLAG");
        managerController.setCrmDone(true);
    }

}
