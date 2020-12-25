/* (C)2020 */
package pers.edison.lazyapp.exception;

public class AppContextConfigException extends LazyAppBaseException {

  private static final long serialVersionUID = 1L;

  public AppContextConfigException() {}

  public AppContextConfigException(String message) {
    super(message);
  }

  public AppContextConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public AppContextConfigException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    String superMsg = super.getMessage();

    return "Please check your lazyApp context config file whether exist. or check that the file format is correct. "
        + superMsg;
  }
}
