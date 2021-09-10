package lx.talx.client.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lx.talx.client.service.IMessageProcessor;
import lx.talx.client.utils.Log;

public class MsgProcessor implements IMessageProcessor {

  // regex pattern recipient user
  private Pattern pUser = Pattern.compile("^@[a-zA-Z]{0,64}\\s");
  private Pattern pMsg = Pattern.compile("\\s.{0,4096}");
  private Matcher m;

  private Date date = new Date();

  private Path root = Paths.get("");
  private Path sptr = Paths.get(File.separator);
  private Path db = Paths.get("db");
  private Path db_root = Paths.get(new String(db.toString() + sptr));

  public MsgProcessor() {

    if (!checkDbDir()) {
      Log.log("Unable to create database for messages");
    }
  }

  @Override
  public void processMessage(String recive) {

    if (recive.matches("^@[a-zA-Z]{3,64}\\s.{0,4096}")) {
      writeFromSender(recive);
    }
  }

  @Override
  public void processCommand(String recive) {

    if (recive.startsWith("/online")) {
      showOnline(recive.substring(8));
    } else if (recive.startsWith("/status")) {
      System.out.print("\n" + recive.substring(7) + "\n\n::>");
    } else if (recive.startsWith("/ping")) {
      // ignore
    }
  }

  @Override
  public List<String> getMessages(int num, String user) {

    Path p = Paths.get("db" + sptr + user);
    List<String> msgs = new ArrayList<>();

    if (Files.exists(p, LinkOption.NOFOLLOW_LINKS)) {

      try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("db" + sptr + user)))) {

        String tmp;
        while ((tmp = in.readLine()) != null) {

          msgs.add(tmp);
          if (msgs.size() == num)
            msgs.remove(0);
        }

        in.close();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return msgs;
  }

  @Override
  public void writeFromSender(String recive) {
    String user = null;
    String message = null;
    long time = date.getTime();

    if ((m = pUser.matcher(recive)).find())
      user = recive.substring(m.start(), m.end());

    if ((m = pMsg.matcher(recive)).find())
      message = recive.substring(m.start(), m.end()).trim();

    write(user, time + " >>> " + message + "\n");
  }

  @Override
  public void writeForRecipient(String user, String message) {

    long time = date.getTime();

    write(user, time + " <<< " + message + "\n");
  }

  private void write(String user, String msg) {

    try (DataOutputStream fout = new DataOutputStream(new FileOutputStream("db" + sptr + user.trim(), true))) {
      fout.write(msg.getBytes());
      fout.flush();
      fout.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean checkDbDir() {
    if (Files.isReadable(root) && Files.isWritable(root)) {
      if (!Files.exists(db, LinkOption.NOFOLLOW_LINKS)
          || (Files.exists(db, LinkOption.NOFOLLOW_LINKS) && !Files.isDirectory(db, LinkOption.NOFOLLOW_LINKS))) {
        try {
          Files.createDirectory(db);
          return true;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return true;
    }

    Log.log("No permission to write in the current program directory");
    System.exit(0);

    return false;
  }

  private void showOnline(String jsonArr) {

    JSONParser p = new JSONParser();

    System.out.println("\n\nUsers:");

    try {
      JSONArray a = (JSONArray) p.parse(jsonArr);

      for (int i = 0; i < a.size(); i++) {
        System.out.println("    @" + a.get(i) + " - online!");
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }

    System.out.print("\n::>");
  }
}
