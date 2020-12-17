package org.molgenis.api.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService{

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendNotification() {
        LOG.info("sendNotification");
    }
}
