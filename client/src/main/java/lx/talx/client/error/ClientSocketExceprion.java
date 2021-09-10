package lx.talx.client.error;

import java.net.SocketException;

public class ClientSocketExceprion extends SocketException {
  public ClientSocketExceprion(String string) {
    super(string);
  }
}
