package lx.talx.server.security;

import java.net.Socket;
import java.util.Arrays;

import org.json.simple.JSONObject;

import lx.talx.server.core.Connection;
import lx.talx.server.core.Server;
import lx.talx.server.utils.Log;
import lx.talx.server.utils.Util;

public class Auth {

  private Connection connection;
  private Socket client;
  private Server server;

  private byte[] buf;

  public Auth(Connection connection) {
    this.connection = connection;
    this.client = connection.getClient();
    this.server = connection.getServer();
  }

  public boolean authorize(byte[] buffer) {

    Log.authorize(buffer);

    // first 15 bytes for command
    String command = new String(buffer, 0, (buffer.length < 15 ? buffer.length : 15));

    if (command.startsWith("/key")) {

      String key = new String(buffer, 15, buffer.length - 15);

      if (server.getAuthProcessor().enable(key)) {

        connection.sendEncrypted("/accepted".getBytes());
        return true;
      }

    } else if (command.startsWith("/auth")) {

      Log.info("Trying login from ".concat(Util.getIp(client)));
      JSONObject tmpUser = Util.parseCredential(buffer, 15);

      if ((buf = server.getAuthProcessor().authenticate(tmpUser)).length != 0) { // send [key] or [0]
        Log.info("Auth: " + (String) tmpUser.get("username") + " / " + (String) tmpUser.get("email"));
        connection.sendEncrypted(buf);
        connection.sendEncrypted("/accepted".getBytes());
        return true;
      }

    } else if (command.startsWith("/new")) {

      JSONObject tmpUser = Util.parseCredential(buffer, 15);
      char[] authcode = server.getAuthProcessor().getAuthCodeAndSendToEmail(tmpUser);
      String note = "Authentication code sent to your email: ".concat((String) tmpUser.get("email")).concat("\n\n");
      connection.sendEncrypted(note.getBytes());
      byte[] responseAuthcode = connection.readEncrypted();

      // if auth right then send [key] or [0]
      if (String.valueOf(authcode).equals(new String(responseAuthcode, 0, responseAuthcode.length))) {
        connection.sendEncrypted(server.getAuthProcessor().create(tmpUser, authcode)); // send key for autologin
                
        Log.info("New User: " + (String) tmpUser.get("username") + " / " + (String) tmpUser.get("email"));
      }
    }

    connection.sendEncrypted(new byte[0]);
    return false;
  }

  public byte[] readSecure() {

    buf = connection.readEncrypted();

    Log.readSec(buf);

    int reciveKeyLength = Util.byteToInt(Arrays.copyOfRange(buf, 0, 4));
    byte[] reciveKey = Arrays.copyOfRange(buf, 4, reciveKeyLength + 4);
    if (server.getAuthProcessor().isKeyEquals(reciveKey)) {
      return Arrays.copyOfRange(buf, 4 + reciveKeyLength, buf.length);
    }
    return new byte[0];
  }

  public void sendSecure(byte[] bytes) {

    if (server.getAuthProcessor().isKeyExist()) {
      connection.sendEncrypted(bytes);
    } else {
      connection.sendEncrypted(new byte[0]);
    }
  }

  public boolean isRevoke() {
    return server.getAuthProcessor().isKeyExist();
  }

  public void revoke() {
    server.getAuthProcessor().disable();
  }
}
