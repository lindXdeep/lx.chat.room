package lx.lindx.talx.server.security;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lx.lindx.talx.server.Connectrion;
import lx.lindx.talx.server.Util;

public class Crypt {

  private Connectrion connection;

  private KeyPair keyPair;
  private SecretKeySpec keyAES;

  public Crypt(Connectrion connectrion) {
    this.connection = connectrion;
  }

  private void setClientPubKey(byte[] publicKeyClient) { // Generate our public key for client

    try {
      PublicKey publicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(publicKeyClient));

      DHParameterSpec dhParamFromServer = ((DHPublicKey) publicKey).getParams();

      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
      keyPairGenerator.initialize(dhParamFromServer);
      keyPair = keyPairGenerator.generateKeyPair();

      generateSharedKey(publicKey);

    } catch (GeneralSecurityException e) {
      Util.log("Connection from:" + Util.getAddress(connection.getClient()) + "rejected because public key is invalid");
      connection
          .sendBytes("Access denied: public key is invalid.".concat(Util.getIp(connection.getClient())).getBytes());
      connection.kill();
    }
  }

  private void generateSharedKey(PublicKey publicKey) {

    try {
      KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(keyPair.getPrivate());
      keyAgree.doPhase(publicKey, true);

      byte[] sharedKeySecret = keyAgree.generateSecret();

      keyAES = new SecretKeySpec(sharedKeySecret, 0, 16, "AES");

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public void encryptConnection() {

    Util.log("Waiting public key from client: " + Util.getAddress(connection.getClient()));
    connection.readNBytes(557);

    setClientPubKey(connection.getBuffer());

    if (connection.getClient().isClosed()) {
      return;
    }

    Util.log("Public key from" + Util.getAddress(connection.getClient()) + "received");

    connection.sendBytes(keyPair.getPublic().getEncoded());
    Util.log("Public key sent to client:" + Util.getAddress(connection.getClient()));

    System.out.println("send AES");
    connection.sendBytes(getKeyAES().getEncoded());
  }

  public SecretKeySpec getKeyAES() {
    return keyAES;
  }
}
