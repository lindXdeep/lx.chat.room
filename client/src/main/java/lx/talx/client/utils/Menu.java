package lx.talx.client.utils;

import lx.talx.client.api.Connect;
import lx.talx.client.net.ServerAddress;

public class Menu {

  public static Connect setConnectParam(Connect client, String... params) {

    if (params.length == 0) {

      return new Connect();

    } else if (params.length == 1 && params[0].equals("--help") || params[0].equals("-h")) {

      Log.printHelp();

    } else if (params.length == 1 && params[0].equals("--about")) {

      Log.printLogo();

    } else if (params.length == 3 && (params[0].equals("--connect") || params[0].equals("-c"))) {

      return new Connect(new ServerAddress(params[1], Integer.valueOf(params[2])));

    } else {
      Log.printError(params);
    }
    throw new RuntimeException("Client service has finished work");
  }

}
