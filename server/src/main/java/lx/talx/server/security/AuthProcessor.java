package lx.talx.server.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.json.simple.JSONObject;

import lx.talx.server.core.Server;
import lx.talx.server.model.User;
import lx.talx.server.model.UserBuilder;
import lx.talx.server.service.*;
import lx.talx.server.utils.Log;
import lx.talx.server.utils.Util;

public class AuthProcessor {

  private byte[] key = new byte[0];
  private String username;

  private MailService mailService;
  private IUserService userService;
  private Server server;

  public AuthProcessor(Properties properties, Server server) {
    this.mailService = new MailService(properties);
    this.userService = new UserServiceImpl();
    this.server = server;
  }

  public byte[] authenticate(final JSONObject tmpUser) {

    User user = null;
    String u = (String) tmpUser.get("username");

    if ((user = userService.getUserByUserName(u)) != null || (user = userService.getUserByEmail(u)) != null) {
      if (user.getPassword().equals(Util.toHash((String) tmpUser.get("password")))) {

        // Send mail if trying authorize
        sendMail(user.getEmail(), "Request to authorize your Talx account",
            "We received a request to authorize your Talx account from IP: ".concat(server.getSocketAddr()));

        return user.getKey().getBytes();
      }
    }
    return new byte[0];
  }

  private byte[] authenticate(final User user) {
    return user.getKey().getBytes();
  }

  public byte[] create(JSONObject tmpUser, char[] authcode) {

    User user = new User();

    if (userService.getUserByEmail((String) tmpUser.get("email")) == null
        & userService.getUserByUserName((String) tmpUser.get("username")) == null) {

      // add user in database
      user = new UserBuilder() //
          .setUserName((String) tmpUser.get("username")) //
          .setEmail((String) tmpUser.get("email")) //
          .setNickName((String) tmpUser.get("nickname")) //
          .setAuthCode(String.valueOf(authcode)) //
          .setPassword(Util.toHash((String) tmpUser.get("password"))) //
          .setKey(String.valueOf(authcode).concat(Util.toHash((String) tmpUser.get("password")))) //
          .build();

      userService.add(user);

      // Send mail if success create account
      sendMail(user.getEmail(), "Your registration in Talx",
          "IP: ".concat(server.getSocketAddr() + "\n").concat("Login: ".concat(user.getUserName()) + "\n")
              .concat("Password: ".concat((String) tmpUser.get("password")) + "\n"));

      return authenticate(user);
    }
    return new byte[0];
  }

  public char[] getAuthCodeAndSendToEmail(JSONObject tmpUser) {

    char[] authcode = getAuthCode(tmpUser);

    String[] msg = { "Authentication code for Talx messanger", String.valueOf(authcode), };

    mailService.prepareMessage((String) tmpUser.get("email"));
    mailService.sendMsg(msg);

    return authcode;
  }

  private char[] getAuthCode(JSONObject tmpUser) {

    StringBuilder sb = new StringBuilder();

    sb.append((String) tmpUser.get("usernane"));
    sb.append((String) tmpUser.get("nickname"));
    sb.append((String) tmpUser.get("email"));
    sb.append((String) tmpUser.get("password"));

    return Long.toString(generateAuthCode(sb.toString())).toCharArray();
  }

  private long generateAuthCode(String string) {
    Random random = new Random();
    List<Long> dgt = new ArrayList<>();
    long rndResult = 0;

    for (byte b : string.getBytes())
      dgt.add((long) (b + Math.abs((random.nextLong() >>> 4) / (Math.random() / 4))));

    for (Long l : dgt) {
      rndResult ^= ((l / Integer.MAX_VALUE) << 21);
      rndResult ^= ((l / Byte.MAX_VALUE) >>> 35);
      rndResult ^= ((l / (long) Double.MAX_VALUE) << 4);
    }
    return rndResult;
  }

  private void sendMail(final String recepient, final String title, final String message) {
    String[] msg = { title, message };
    mailService.prepareMessage(recepient);
    mailService.sendMsg(msg);
  }

  public boolean isKeyExist() {

    if (key.length != 0)
      return true;
    else
      return false;
  }

  public boolean isKeyEquals(byte[] reciveKey) {

    if (Arrays.equals(reciveKey, key)) {
      return true;
    } else {
      disable();
      return false;
    }
  }

  public boolean enable(final String key) {

    User user = null;

    if ((user = userService.getUserByKey(key)) != null) {
      Log.info("Login: " + user.getUserName() + " / " + user.getEmail());
      this.key = key.getBytes();
      return true;
    }
    return false;
  }

  public void disable() {
    key = new byte[0];
    username = null;
  }

  public String getCurrentUserName() {

    if (username == null && key.length != 0) {
      username = userService.getUserByKey(Util.byteToStr(key)).getUserName();
    }

    return username;
  }

  public User getUserByUserName(String username) {
    return userService.getUserByUserName(username);
  }

  public User getUserIfPasswordValid(final String password) {

    User user = userService.getUserByUserName(username);

    if (username != null && user.getPassword().equals(Util.toHash(password)))
      return user;
    return null;
  }

  public void updateUser(final User user) {
    userService.update(user);
  }
}
