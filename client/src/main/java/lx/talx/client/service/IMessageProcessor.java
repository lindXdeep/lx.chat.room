package lx.talx.client.service;

import java.util.List;

/**
 * IMessageProcessor
 */
public interface IMessageProcessor {

  void processMessage(String recive);

  void processCommand(String recive);

  List<String> getMessages(int num, String user);

  void writeFromSender(String recive);

  void writeForRecipient(String user, String message);
}