package org.molgenis.api.notification.v1;

import org.molgenis.util.exception.BadRequestException;

@SuppressWarnings("java:S110")
public class NotificationException extends BadRequestException {
  private static final String ERROR_CODE = "NAPI01";

  NotificationException() {
    super(ERROR_CODE);
  }

  @Override
  public String getMessage() {
    return String.format("");
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {};
  }
}
