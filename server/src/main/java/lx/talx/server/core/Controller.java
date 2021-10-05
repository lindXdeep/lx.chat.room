package lx.talx.server.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import lx.talx.server.model.User;
import lx.talx.server.utils.Util;

public class Controller {

  // regex pattern recipient user
  private Pattern pUsr = Pattern.compile("^@[a-zA-Z]{0,255}\\s");
  private Pattern pMsg = Pattern.compile("\\s.{0,4096}");

  private Server server;

  public Controller(Server server) {
    this.server = server;
  }

  public void processMessage(String msg) {

    Matcher m;
    String sender = server.getAuthProcessor().getCurrentUserName();
    String recipent = null;
    String message = null;

    if (msg.matches("^@[a-zA-Z]{3,64}\\s.{0,4096}")) {

      if ((m = pUsr.matcher(msg)).find())
        recipent = msg.substring(m.start(), m.end()).toLowerCase();

      if ((m = pMsg.matcher(msg)).find())
        message = msg.substring(m.start(), m.end()).toLowerCase();

      if (recipent.matches("^@all\\s")) {
        server.getConnectionPool().sendPublicMessage(sender, message);
      } else if (recipent.matches("^@[a-zA-Z]{0,64}\\s")) {
        server.getConnectionPool().sendPrivateMessage(sender, recipent.substring(1).trim(), message);
      }

    } else if (msg.matches("^/online")) {
      server.getConnectionPool().executeSendUsersOnline(sender, msg);

    } else if (msg.matches("^/disconnect")) {

      sender = server.getAuthProcessor().getCurrentUserName();

      server.getConnectionPool().killAllUsersConnections(sender);
      server.getConnectionPool().delete(sender);
      server.getAuthProcessor().disable();
    } else if (msg.matches("^/ping")) {
      server.getConnectionPool().ping(sender);
    } else if (msg.startsWith("/edit nickname")) {

      JSONArray arr = (JSONArray) JSONValue.parse(msg.substring(14));

      JSONObject param = (JSONObject) arr.get(0);
      String oldNick = param.get("old nickname").toString();
      String newNick = param.get("new nickname").toString();

      String password = ((JSONObject) arr.get(1)).get("password").toString();
      User user = server.getAuthProcessor().getUserIfPasswordValid(password);

      if (user.getNickName().equals(oldNick)) {
        user.setNickName(newNick);
        server.getAuthProcessor().updateUser(user);
      }
    } else if (msg.startsWith("/edit password")) {

      JSONArray arr = (JSONArray) JSONValue.parse(msg.substring(14));

      JSONObject param = (JSONObject) arr.get(0);
      String oldPass = param.get("old password").toString();
      String newPass = param.get("new password").toString();

      User user = server.getAuthProcessor().getUserIfPasswordValid(oldPass);

      if (user != null) {
        user.setPassword(Util.toHash(newPass));
        server.getAuthProcessor().updateUser(user);
      }
    }
  }
}