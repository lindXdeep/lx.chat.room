package lx.lindx.talx.client.net;

import lx.lindx.talx.client.error.ClientSocketExceprion;

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

public interface IMsgProtocol {

  void send(byte[] bytes) throws ClientSocketExceprion;

  byte[] read();

  void sendMsg(String msg) throws ClientSocketExceprion;

  void executeKeyExchange() throws ClientSocketExceprion;

  void killIsNotEncrypted();
}
