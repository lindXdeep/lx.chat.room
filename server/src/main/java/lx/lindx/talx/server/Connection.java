package lx.lindx.talx.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.security.GeneralSecurityException;

import lx.lindx.talx.server.error.ClientSocketExceprion;
import lx.lindx.talx.server.security.Crypt;

public class Connection extends Thread {

  private Crypt crypt;
  private IMsgProtocol protocol;

  private Socket client;
  private Server server;
  private byte[] buffer;

  private BufferedOutputStream out;
  private InputStream in;

  private boolean encrypted;

  public Connection(Socket client, Server server) {

    this.client = client;
    this.server = server;

    this.crypt = new Crypt();
    this.protocol = new Protocol(this, crypt);

    try {
      this.out = new BufferedOutputStream(client.getOutputStream());
      this.in = client.getInputStream();

    } catch (IOException e) {
      e.printStackTrace();
    }
    Util.log("Create I/O connection with " + client.toString());
  }

  @Override
  public void run() {

    executeKeyExchange();

    menu();

    System.out.println("-----------end menu-----------");
  }

  private void executeKeyExchange() {

    Util.log("Waiting public key from client: " + Util.getAddress(client));
    readNBytes(557);

    try {

      crypt.setClientPubKey(buffer);

    } catch (GeneralSecurityException e) {
      Util.log("Connection from:" + Util.getAddress(client) + "rejected because public key is invalid");
      sendBytes("Access denied: public key is invalid.".concat(Util.getIp(client)).getBytes());
      kill();
    }

    if (client.isClosed())
      return;

    Util.log("Public key from" + Util.getAddress(client) + "received");

    sendBytes(crypt.getPubKeyEncoded());
    Util.log("Public key sent to client:" + Util.getAddress(client));

    // TODO: delete
    System.out.println("send AES");
    sendBytes(crypt.getKeyAES().getEncoded());

    encrypted = true;
  }

  private void menu() {
    try {

      sendMsg(Util.getLogo());
      sendMsg(Util.getInstruction());

      while (true) {

        sendCursor(); // data will be in buffer

        switch (new String(buffer, 0, buffer.length).trim()) {
          case "/help":
            sendMsg(Util.getHelp());
            break;
          case "/about":
            sendMsg(Util.getLogo().substring(140, Util.getLogo().length() - 1));
            break;
          case "/new":
            createAccount();
            break;
          case "/auth":
            authorize();
            break;
          case "/end":
            // TODO: kill connection
            break;
          default:
            sendMsg(Util.getInstruction());
        }

        Util.logCommand(this);

      }

    } catch (ClientSocketExceprion e) {
      Util.log(e.getMessage());
    }
  }

  private void createAccount() {

    for (String b : askCredentials()) {
      System.out.print(b + " ");
    }

  }

  private void authorize() {

  }

  private String[] askCredentials() {

    String mail = null;
    String pass = null;
    String authcode = null;
    String login = null;

    mail = askConcreteCredentialType("Email", 32);
    pass = askConcreteCredentialType("Password", 64);
    authcode = askConcreteCredentialType("Authcode", 6);
    login = askConcreteCredentialType("Login", 16);

    return new String[] { mail, pass, authcode, login };
  }

  private String askConcreteCredentialType(String credentialType, int bufferSize) {

    try {
      clearBuffer(bufferSize);
      sendMsg(credentialType + ": ");
      in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(buffer, 0, buffer.length).trim();
  }

  public void readNBytes(final int length) {

    clearBuffer(length);

    try {
      in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendBytes(final byte[] bytes) {

    try {
      out.write(bytes);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Read message from cliet
   * 
   * @throws ClientSocketExceprion
   */
  private void sendCursor() throws ClientSocketExceprion {
    clearBuffer();
    sendMsg("\n > ");
    try {
      in.read(buffer);
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Client" + Util.getAddress(client) + "-/-> Server:[" + server.getPort() + "] ::: Connection reset");
    }
  }

  /**
   * Send message to client
   * 
   * @param msg
   * @throws ClientSocketExceprion
   */
  public void sendMsg(String msg) throws ClientSocketExceprion {

    // System.out.println("\n-------+++---------------\n");

    // protocol.sendMsg(msg);

    // MsgProtocol

    // byte[] size = new byte[10];
    // byte[] key = new byte[18];
    // byte[] ms = new byte[100];

    // byte[] result = new byte[1024];

    // size = new byte[] {
    // (byte)((s >> 24) & 0xff),
    // (byte)((s >> 16) & 0xff),
    // (byte)((s >> 8) & 0xff),
    // (byte)((s >> 0) & 0xff),
    // };

    // System.out.println("to int");
    // for (byte b : size) {
    // System.out.print(b + " ");
    // }

    // int r= (int)( // NOTE: type cast not necessary for int
    // (0xff & size[0]) << 24 |
    // (0xff & size[1]) << 16 |
    // (0xff & size[2]) << 8 |
    // (0xff & size[3]) << 0
    // );

    // System.out.println(r);

    // BigInteger b = BigInteger.valueOf(Integer.MAX_VALUE);

    // System.out.println(String.valueOf(Integer.MAX_VALUE));

    // int i =0;
    // for (byte q : String.valueOf(Integer.MAX_VALUE).getBytes()) {
    // System.out.print(q + " ");
    // i++;
    // }
    // System.out.println(i);

    // System.out.println(String.valueOf(Integer.MAX_VALUE).getBytes().length);

    // System.out.println("\n---------====-------------\n");

    byte[][] cortege = crypt.encrypt(msg.getBytes());

    // for (byte cs : cortege[1]) {
    // System.out.print(cs + " ");
    // }

    // System.out.println(cortege[0].length);
    // System.out.println(cortege[1].length);

    try {
      out.write(cortege[0]);
      out.flush();
      out.write(cortege[1]);
      out.flush();
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Can't write, because connection with" + Util.getAddress(client) + "has already closed it");
    }
  }

  private void clearBuffer() {
    buffer = new byte[32];
  }

  private void clearBuffer(int size) {
    buffer = new byte[size];
  }

  public Socket getClient() {
    return this.client;
  }

  public byte[] getBuffer() {
    return this.buffer;
  }

  public void kill() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isEncrypted() {
    return encrypted;
  }
}
