package lx.talx.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import lx.talx.client.api.Auth;
import lx.talx.client.api.Connect;
import lx.talx.client.error.CantWriteBytesExeption;
import lx.talx.client.error.WrongCommandException;
import lx.talx.client.service.ICommandLine;
import lx.talx.client.utils.Log;
import lx.talx.client.utils.Util;

public class Cli implements ICommandLine {

  private Connect connect;

  private int minMailConstraint = 1;
  private int maxMailConstraint = 255;

  private Scanner cl = new Scanner(System.in);

  private byte[] buf;

  private Auth auth;
  private BufferedReader bufIn = new BufferedReader(new InputStreamReader(System.in));

  private Matcher m;

  // regex pattern recipient user
  private Pattern pUser = Pattern.compile("^@[a-zA-Z]{0,64}\\s");
  private Pattern pMsg = Pattern.compile("\\s.{0,4096}");

  // regex pattern for special symbols
  private Pattern spc = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

  // regex pattern for email RFC822 compliant right format
  private Pattern ptr = Pattern.compile(
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  public Cli(Connect connect) {
    this.connect = connect;
    this.auth = connect.getAuth();
    console();
  }

  private void console() {

    enterToAccount();

    System.out.println("------------ Console mode ------------");
    while (true) {
      Util.printCursor();
      while (cl.hasNext()) {
        try {
          execute(cl.nextLine());
        } catch (WrongCommandException e) {
          Log.error(e.getMessage());
          Util.printCursor();
        }
      }
    }
  }

  @Override
  public void execute(String command) throws WrongCommandException {
    if (command.matches("^/signin") || command.matches("^1")) {
      if (connect.getStatus() & !auth.isLoginStatus()) {
        auth();
      } else {
        status();
      }
    } else if (command.matches("^/signup") || command.matches("^2")) {
      if (auth.isLoginStatus()) {
        logout();
        connect();
      }
      signup();
    } else if (command.matches("^/status") || command.matches("^3")) {
      status();
    } else if (command.matches("^/connect") || command.matches("^4")) {
      connect();
    } else if (command.matches("^/connect\\s\\d{2,5}") || command.matches("^5\\s\\d{2,5}")) {
      connect(Integer.parseInt(command.split("\\s")[1]));
    } else if (command.matches("^/disconnect") || command.matches("^6")) {
      disconnect();
    } else if (command.matches("^/reconnect") || command.matches("^7")) {
      reconnect();
    } else if (command.matches("^/logout") || command.matches("^8")) {
      logout();
    } else if (command.matches("^/exit") || command.matches("^9")) {
      exit();
    } else if (command.matches("^/help") || command.matches("^10")) {
      help();
    } else if (command.matches("^@[a-zA-Z]{3,64}\\s.{0,4096}")) {
      sendMessage(command);
    } else if (command.matches("^/online")) {
      online();
    } else if (command.matches("^/read\\s@[a-zA-Z]{3,64}\\s\\d{1,4}")) {
      read(command);
    } else if (command.matches("^/read\\s@[a-zA-Z]{3,64}")) {
      read(command.concat(" " + 10));
    } else {
      throw new WrongCommandException(command);
    }

    Util.printCursor();
  }

  private void read(String command) {

    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
    Pattern pUser = Pattern.compile("@[a-zA-Z]{0,64}\\s");
    Pattern pNum = Pattern.compile("\\s\\d{1,4}");

    String user = null;
    String numlastMsgs = null;

    if ((m = pUser.matcher(command)).find())
      user = command.substring(m.start(), m.end()).trim();

    if ((m = pNum.matcher(command)).find())
      numlastMsgs = command.substring(m.start(), m.end()).trim();

    List<String> msgs = auth.getMsgProcessor().getMessages(Integer.parseInt(numlastMsgs), user);

    System.out.println("\nMessages:\n");

    String resDate = null;
    String resMsg = null;

    // view messages
    for (String s : msgs) {

      String msgFromSender = "^\\d{10,20}\\s>>>\\s.{1,4096}$"; // >>>
      String msgForRecipient = "^\\d{10,20}\\s<<<\\s.{1,4096}$"; // <<<

      // parse Date
      Pattern pSrcDate = Pattern.compile("^\\d{10,20}\\s");
      if ((m = pSrcDate.matcher(s)).find())
        resDate = dateFormat.format(Long.parseLong(s.substring(m.start(), m.end()).trim()));

      // parse message
      Pattern pSrcMsg = Pattern.compile("\\s(<<<|>>>)\\s.{1,4096}$");
      if ((m = pSrcMsg.matcher(s)).find())
        resMsg = s.substring(m.start() + 4, m.end()).trim();

      if (s.matches(msgFromSender)) { // >>>
        System.out.println("[" + resDate + "]" + user + " <<< " + resMsg);

      } else if (s.matches(msgForRecipient)) { // <<<
        System.out.println("[" + resDate + "]" + user + " >>> " + resMsg);
      }
    }
  }

  private void online() {
    connect.sendSecure("/online".getBytes());
  }

  private void sendMessage(String command) {

    String user = null;
    String message = null;

    if ((m = pUser.matcher(command)).find())
      user = command.substring(m.start(), m.end());

    if ((m = pMsg.matcher(command)).find())
      message = command.substring(m.start(), m.end()).trim();

    try {
      connect.sendMessage(user, message);
    } catch (CantWriteBytesExeption e) {

    }

    writeForRecipient(user, message);

  }

  private void connect() {
    if (!connect.getStatus()) {
      connect.connect();
      enterToAccount();
    } else {
      System.out.println("\nConnection to " + connect.getAddress().getHost() + " is already open!\n");
    }
  }

  private void connect(int port) {
    if (!connect.getStatus()) {
      connect.connect(port);
      enterToAccount();
    } else {
      System.out.println("\nActive connection to " + connect.getAddress().getHost() + "!\n");
    }
  }

  private void reconnect() {

    disconnect();

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    connect();
  }

  private void disconnect() {

    if (connect.getStatus()) {
      connect.sendSecure("/disconnect".getBytes());
    }

    if (!connect.disconnect()) {
      System.out.println("\nNo connection to server\n");
    }
  }

  private void status() {

    if (connect.getStatus()) {

      System.out.print("\nConnected on: " + connect.getAddress());
      if (auth.isLoginStatus()) {
        System.out.println(" ->  login in account\n");
      } else {
        System.out.println(" ->  no authorization\n");
      }
    } else {
      System.out.println("\ndisconnected\n");
    }
  }

  private void exit() {
    disconnect();
    System.out.println("\nbye...\n");
    System.exit(0);
  }

  private void auth() {
    System.out.println("\n--------- Login for Talx ---------\n");

    if (!auth.auth(prepareCredentionalData("Username", "Password"))) {
      signup();
    } else {
      System.out.println("Login successful!\n");
    }
  }

  private void signup() {

    System.out.println("\n--------- Sign up for Talx ---------\n");

    boolean allowedSymbols = true;

    JSONObject user = null;

    while (allowedSymbols) {

      user = prepareCredentionalData("NickName", "Username", "Email", "Password");

      allowedSymbols = (

      spc.matcher(((String) user.get("nickname"))).find() | spc.matcher(((String) user.get("username"))).find()

      );

      if (allowedSymbols) {
        System.out.println("Forbidden characters are present in the \"NickName\" or \"Username\"");
      } else {
        break;
      }
    }

    auth.signup(user);

    // AuthCode:
    buf = connect.read();
    System.out.print(Util.byteToStr(buf).concat("AuthCode: "));
    if (auth.authCode(dataEnter(buf))) {
      System.out.println("Login!");
    } else {
      System.out.println("Authorization code is not correct or user already exist");
    }
  }

  private void logout() {
    auth.removeKey();
    disconnect();
  }

  private void enterToAccount() {
    if (connect.getStatus()) {
      if (!auth.enterToAccount()) {
        System.out.println("Auth key not exist. Please login\n");
        System.out.println("Type 11 or /help - for more information");
      }
    }
  }

  private JSONObject prepareCredentionalData(String... parameters) {

    JSONObject user = new JSONObject();

    for (String item : parameters) {
      System.out.print(item.concat(": "));
      user.put(item.toLowerCase(), new String(dataEnter(item.concat(": ").getBytes())));
    }

    return user;
  }

  private byte[] dataEnter(byte[] requestMessage) {

    String str = null;

    try {

      if (Util.byteToStr(requestMessage).equals("Email:\s")) {

        while (!ptr.matcher(str = bufIn.readLine()).matches()) {
          Log.error("Invalid email format");
          System.out.print(new String(requestMessage, 0, requestMessage.length));
        }

      } else {

        while ((str = bufIn.readLine()).length() < minMailConstraint || str.length() > maxMailConstraint) {

          if (str.length() < minMailConstraint) {
            Log.error("String cannot be shorter than " + minMailConstraint + " characters");
          } else if (str.length() > maxMailConstraint) {
            Log.error("String cannot be longer than " + maxMailConstraint + " characters");
          }

          System.out.print(new String(requestMessage, 0, requestMessage.length));
        }
      }

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return str.getBytes();
  }

  private void help() {

    String[] help = {

        " 1. /signin             - Authentication",

        " 2. /sigup              - Authorization",

        " 3. /status             - Сurrent status",

        " 4. /connect            - Try connect to the server using last address:" + connect.getAddress().toString(),

        " 5. /connect <PORT>     - Try connect to the server using custom port",

        " 6. /disconnect         - Disconnect from the Server",

        " 7. /reconnect          - Reconnect to the Server",

        " 8. /logout             - Logout from the user account",

        " 9. /exit               - Exit from the Talx",

        "10. /help               - Help",

        "------------------------ Online options ------------------------",

        "@<username> <message>   - Send private message for user",

        "@all <message>          - Sand public message for all contacts",

        "/online                 - Show online users",

        "/read <username>        - read last 10 messages from <username>",

        "/read <username> <num>  - read last <num> messages from <username>" };

    for (String h : help) {
      System.out.println(h);
    }
  }

  private void writeForRecipient(String user, String message) {
    new Thread(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      auth.getMsgProcessor().writeForRecipient(user, message);
    }).start();
  }
}
