package lx.talx.server.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailService {

  private String mailAccount;
  private char[] password;
  private Session session;
  private String recipient;
  private Message message;

  public MailService(Properties properties) {

    this.mailAccount = properties.getProperty("talxMailAccount");
    this.password = properties.getProperty("password").toCharArray();

    this.session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mailAccount, String.valueOf(password));
      }
    });
  }

  public Message prepareMessage(final String recipient) {

    this.recipient = recipient;

    Message message = new MimeMessage(this.session);

    try {
      message.setFrom(new InternetAddress(this.mailAccount));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    return message;
  }

  public void sendMsg(final String[] msg) {

    try {
      this.message = prepareMessage(this.recipient);
      this.message.setSubject(msg[0]);
      this.message.setText(msg[1]);

      Transport.send(message);

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
