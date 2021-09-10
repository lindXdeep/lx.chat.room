package lx.talx.client.api;

import java.nio.ByteBuffer;

import lx.talx.client.error.ClientSocketExceprion;
import lx.talx.client.net.Connection;
import lx.talx.client.net.Protocol;
import lx.talx.client.net.ServerAddress;
import lx.talx.client.utils.Log;
import lx.talx.client.utils.Util;

public class Connect {

  private ServerAddress address;
  private Connection connection;

  private Protocol protocol;

  private Auth auth;

  public Connect() {
    this(new ServerAddress("127.0.0.1", 8181)); // default
  }

  public Connect(final ServerAddress serverAddress) {
    this.address = serverAddress;
    this.connection = new Connection(address);
    this.protocol = new Protocol(connection);

    this.auth = new Auth(this);

    connect();
  }

  // TODO: Connections

  public void connect(int port) {
    address.setPort(port);
    connect();
  }

  public boolean connect() {
    if (!connection.getStatus()) {
      if (connection.connect()) {
        try {
          this.protocol.executeKeyExchange();
          Log.log("Key exchange Success");
        } catch (ClientSocketExceprion e) {
          e.printStackTrace();
          return false;
        }
        return true;
      }
    }
    return false;
  }

  public boolean disconnect() {
    return connection.kill();
  }

  // TODO: Send and recive
  public void sendMessage(String user, String message) {
    ByteBuffer buf = ByteBuffer.allocate(64 + 4 + message.length());
    buf.put(0, Util.strToByte(user));
    buf.put(64, Util.intToByte(message.length()));
    buf.put(68, Util.strToByte(message));
    sendSecure(buf.array());
  }

  public void sendSecure(byte[] bytes) {

    ByteBuffer b = ByteBuffer.allocate(4 + auth.getKey().length + bytes.length);
    b.put(Util.intToByte(auth.getKey().length)); // 4
    b.put(4, auth.getKey());
    b.put(4 + auth.getKey().length, bytes);

    send(b.array());
  }

  public void send(byte[] bytes) {
    protocol.sendEncrypted(bytes);
  }

  public byte[] read() {
    return protocol.readEncrypted();
  }

  public Connection getConnection() {
    return connection;
  }

  public ServerAddress getAddress() {
    return address;
  }

  public boolean getStatus() {
    if (connection.getStatus())
      return true;
    else
      return false;
  }

  public Auth getAuth() {
    return auth;
  }

}
