package lx.talx.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
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

  private Pattern pUser;   // regex pattern recipient user
  private Pattern pMsg;
  private Pattern spc; // regex pattern for special symbols
  private Pattern ptr; // regex pattern for email RFC822 compliant right format

  private Properties c = new Properties();
  private Properties p = new Properties();

  public Cli(Connect connect) {
    this.connect = connect;
    this.auth = connect.getAuth();

    try (InputStream in_comm = Cli.class.getClassLoader().getResourceAsStream("command.properties");
        InputStream in_prop = getClass().getClassLoader().getResourceAsStream("patterns.properties")) {

      if (in_comm == null || in_prop == null) {
        System.out.println("Sorry, unable to find properties");
        return;
      }

      c.load(in_comm);
      p.load(in_prop);

    } catch (IOException e) {
      Log.error("Error in property file");
    }

    pUser = Pattern.compile(p.getProperty("pUser")); // regex pattern recipient user
    pMsg = Pattern.compile(p.getProperty("pMsg"));
    spc = Pattern.compile(p.getProperty("spc")); // regex pattern for special symbols
    ptr = Pattern.compile(p.getProperty("ptr")); // regex pattern for email RFC822 compliant right format

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

    if (command.matches(c.getProperty("help")) || command.matches("^0")) {
      System.out.printf("\n" + p.getProperty("help") + "\n", connect.getAddress().toString());
    } else if (command.matches(c.getProperty("status")) || command.matches("^1")) {
      status();
    } else if (command.matches(c.getProperty("signin")) || command.matches("^2")) {
      if (connect.getStatus() & !auth.isLoginStatus()) {
        auth();
      } else {
        status();
      }
    } else if (command.matches(c.getProperty("signup")) || command.matches("^3")) {
      if (auth.isLoginStatus()) {
        logout();
        connect();
      }
      signup();
    } else if (command.matches(c.getProperty("connect_def")) || command.matches("^4")) {
      connect();
    } else if (command.matches(c.getProperty("connect_port")) || command.matches("^5\\s\\d{2,5}")) {
      connect(Integer.parseInt(command.split("\\s")[1]));
    } else if (command.matches(c.getProperty("disconnect")) || command.matches("^6")) {
      disconnect();
    } else if (command.matches(c.getProperty("reconnect")) || command.matches("^7")) {
      reconnect();
    } else if (command.matches(c.getProperty("logout")) || command.matches("^8")) {
      logout();
    } else if (command.matches(c.getProperty("exit")) || command.matches("^9")) {
      exit();
    } else if (command.matches(c.getProperty("snd_usr_msg"))) {
      sendMessage(command);
    } else if (command.matches(c.getProperty("online"))) {
      online();
    } else if (command.matches(c.getProperty("read_msg_user_num"))) {
      read(command);
    } else if (command.matches(c.getProperty("read_msg_user_all"))) {
      read(command.concat(" " + 10));
    } else if (command.matches(c.getProperty("edit"))) {
      edit(command);
    } else if (command.matches(c.getProperty("delete"))) {
      delete(command);
    } else if (command.matches(c.getProperty("whoami"))) {
      whoami(command);
    } else {
      throw new WrongCommandException(command);
    }

    Util.printCursor();
  }

  private void whoami(String command) {
    connect.sendSecure("/whoami".getBytes());
  }

  private void delete(String command) {
    System.out.println("\nYou are going to permanently delete your account.\n");
    JSONObject password = prepareCredentionalData("password");
    connect.sendSecure(command.concat(password.toJSONString()).getBytes());
  }

  private void edit(String command) {

    String param = command.length() > 6 ? command.substring(6) : null;
    String passParam = command.length() > 6 && param.matches("password") ? param : null;
    String nickparam = command.length() > 6 && param.matches("nickname") ? param : null;

    JSONArray update = new JSONArray();

    if (passParam != null || nickparam != null) {
      update.clear();

      JSONObject updateParam = getParamsForUpdate(param);
      update.add(updateParam);

      if (nickparam != null)
        update.add(prepareCredentionalData("password"));

      connect.sendSecure(command.concat(update.toJSONString()).getBytes());
    } else {
      System.out.printf(p.getProperty("note_param"), command.substring(6));
    }
  }

  public JSONObject getParamsForUpdate(String param) {
    return prepareCredentionalData("old ".concat(param), "new ".concat(param));
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
      Thread.currentThread().interrupt();
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

      allowedSymbols = (spc.matcher(((String) user.get("nickname"))).find()
          | spc.matcher(((String) user.get("username"))).find());

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
        System.out.println(p.getProperty("note"));
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
