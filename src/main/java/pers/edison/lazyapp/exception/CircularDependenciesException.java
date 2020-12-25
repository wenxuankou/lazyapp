/* (C)2020 */
package pers.edison.lazyapp.exception;

public class CircularDependenciesException extends LazyAppBaseException {

  private static final long serialVersionUID = 1L;

  public CircularDependenciesException() {}

  public CircularDependenciesException(String message) {
    super(message);
  }

  public CircularDependenciesException(String message, Throwable cause) {
    super(message, cause);
  }

  public CircularDependenciesException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    String superMsg = super.getMessage();

    return "Please check your dependencies of bean. " + superMsg;
  }
}
