package org.molgenis.api.notification.v1;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.molgenis.api.notification.NotificationService;
import org.molgenis.test.AbstractMockitoTest;
import org.molgenis.util.i18n.MessageSourceHolder;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class NotificationControllerTest extends AbstractMockitoTest {
  @Mock private NotificationService notificationService;
  @Mock private MessageSource messageSource;
  private NotificationController notificationController;

  @BeforeEach
  void setUpBeforeMethod() {
    notificationController = new NotificationController(notificationService);
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
    MessageSourceHolder.setMessageSource(messageSource);
  }

  @Test
  void testSendNotification() {
    notificationController.sendNotification();
    verify(notificationService).sendNotification();
  }
}
