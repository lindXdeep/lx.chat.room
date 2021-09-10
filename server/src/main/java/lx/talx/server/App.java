package lx.talx.server;

import java.io.InputStream;
import java.util.Properties;

import lx.talx.server.core.Server;
import lx.talx.server.error.WrongPortException;
import lx.talx.server.utils.Log;

public class App {

  public static Properties properties;
  private static String propMail = "mail.properties";

  private static Server server;
  private final static int PORT = 8181; // default
  private static int length;

  public static void main(String... args) {

    try (InputStream input = App.class.getClassLoader().getResourceAsStream(propMail)) {

      if (input == null) {
        Log.info("Unable to find mail.properties");
        return;
      }
      
      properties = new Properties();
      properties.load(input);

    } catch (Exception e) {
      e.printStackTrace();
    }

    length = args.length;

    if (length == 1) {
      int i = 0;
      char ch;
      while ((i++ < length)) {
        if (!Character.isDigit((ch = args[0].charAt(i))))
          throw new WrongPortException();

        server = new Server(Integer.parseInt(args[0]), properties);
      }
    } else {
      server = new Server(PORT, properties);
    }
    server.start();
  }
}