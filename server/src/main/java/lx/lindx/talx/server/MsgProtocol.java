package lx.lindx.talx.server;

import lx.lindx.talx.server.security.Crypt;

/**
 * Message protocol.
 * 
 * max size 2147483647
 *
 * [127][-1][-1][-1] - First 4 bytes [0 - 3] - for size message
 * 
 * [][][][][][][][][][][][][][][][][][] - Second 18 bytes [4 - 21] for Encoded
 * Params
 * 
 * [34][54][12][34]..... - last all bytes [22 - 2147483625]- for message
 */

public class MsgProtocol {

  private Connection connection;

  private byte[] buffer;

  public MsgProtocol(Connection connection) {
    this.connection = connection;
    this.buffer = new byte[1024]; // default
  }

  public void sendMsg(String msg) {
 
  }


}
