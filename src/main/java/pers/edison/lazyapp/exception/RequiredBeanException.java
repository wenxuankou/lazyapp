/* (C)2020 */
package pers.edison.lazyapp.exception;

public class RequiredBeanException extends LazyAppBaseException {

  private static final long serialVersionUID = 1L;

  public RequiredBeanException() {}

  public RequiredBeanException(String message) {
    super(message);
  }

  public RequiredBeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public RequiredBeanException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    String superMsg = super.getMessage();

    return "We can not find your bean which you would. " + superMsg;
  }
}
