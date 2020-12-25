/* (C)2020 */
package pers.edison.lazyapp.exception;

public class LazyAppBaseException extends Exception {

  private static final long serialVersionUID = 1L;

  public LazyAppBaseException() {}

  public LazyAppBaseException(String message) {
    super(message);
  }

  public LazyAppBaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public LazyAppBaseException(Throwable cause) {
    super(cause);
  }
}
