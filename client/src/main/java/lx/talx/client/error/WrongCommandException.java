package lx.talx.client.error;

public class WrongCommandException extends RuntimeException {
  public WrongCommandException(String str) {
    super("Error: No such command exists: " + str);
  }
}
