package org.molgenis.security.account;

import org.molgenis.util.exception.CodedRuntimeException;

/** @deprecated use class that extends from {@link CodedRuntimeException} */
@Deprecated
public class EmailAlreadyExistsException extends Exception {
  private static final long serialVersionUID = 1L;

  public EmailAlreadyExistsException() {}

  public EmailAlreadyExistsException(String message) {
    super(message);
  }

  public EmailAlreadyExistsException(Throwable cause) {
    super(cause);
  }

  public EmailAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmailAlreadyExistsException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
