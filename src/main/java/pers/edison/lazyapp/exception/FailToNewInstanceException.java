/* (C)2020 */
package pers.edison.lazyapp.exception;

public class FailToNewInstanceException extends LazyAppBaseException {

  private static final long serialVersionUID = 1L;

  public FailToNewInstanceException() {}

  public FailToNewInstanceException(String message) {
    super(message);
  }

  public FailToNewInstanceException(String message, Throwable cause) {
    super(message, cause);
  }

  public FailToNewInstanceException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    String superMsg = super.getMessage();

    return "Please verify your beans that setter and constructor could be work. " + superMsg;
  }
}
