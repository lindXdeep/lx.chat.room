package lx.talx.client.net;

public class ServerAddress {
  private final String host;
  private int port;

  public ServerAddress(final String host, final int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  @Override
  public String toString() {
    return " [".concat(getHost()).concat(":").concat(String.valueOf(getPort())).concat("] ");
  }

  public void setPort(int port) {
    this.port = port;
  }
}
