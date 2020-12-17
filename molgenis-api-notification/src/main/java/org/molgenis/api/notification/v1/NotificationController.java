package org.molgenis.api.notification.v1;

import static java.util.Objects.requireNonNull;

import org.molgenis.api.ApiController;
import org.molgenis.api.ApiNamespace;
import org.molgenis.api.notification.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(NotificationController.API_ENTITY_PATH)
public class NotificationController extends ApiController {
  private static final String API_ENTITY_ID = "notification";
  public static final String API_ENTITY_PATH = ApiNamespace.API_PATH + '/' + API_ENTITY_ID;

  private final NotificationService notificationService;

  NotificationController(NotificationService notificationService) {
    super(API_ENTITY_ID, 1);
    this.notificationService = requireNonNull(notificationService);
  }

  @Transactional
  @PostMapping("/notify")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void sendNotification() {
    notificationService.sendNotification();
  }
}
