package lx.talx.server.error;

public class WrongPortException extends RuntimeException {

  public WrongPortException(String string) {
    super(string);
  }

  public WrongPortException() {
    this("The port cannot contain symbol other than numbers.");
  }
}
