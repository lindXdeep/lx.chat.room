package lx.lindx.talx.client.net;

public class UserAddress {
  private final String host;
  private final int port;

  public UserAddress(final String host, final int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public String getStringAddr() {
    return " [".concat(getHost()).concat(":").concat(String.valueOf(getPort())).concat("] ");
  }
}
