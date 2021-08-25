package lx.lindx.talx.client.security;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import lx.lindx.talx.client.Util;
import lx.lindx.talx.client.net.Connection;

/**
 * Crypt module
 */
public class Crypt {

  private KeyAgreement keyAgree;

  private byte[] pubkeyEncoded;

  private SecretKeySpec keyAES;

  private Connection connection;

  public Crypt(Connection connection) {

    this.connection = connection;

    try {

      KeyPair keyPair = KeyPairGenerator.getInstance("DH").generateKeyPair();

      keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(keyPair.getPrivate());

      pubkeyEncoded = keyPair.getPublic().getEncoded();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public void setServerPubKey(byte[] serverPubKey) {

    try {
      KeyFactory keyFactory = KeyFactory.getInstance("DH");

      PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(serverPubKey));

      keyAgree.doPhase(pubKey, true);

      byte[] sharedKeySecret = keyAgree.generateSecret();

      keyAES = new SecretKeySpec(sharedKeySecret, 0, 16, "AES");

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public SecretKeySpec getKeyAES() {
    return keyAES;
  }

  public void encryptConnection() {

    Util.toConsole("Sending public key to server");
    connection.sendBytes(pubkeyEncoded);

    connection.readNBytes(557);
    this.setServerPubKey(connection.getBuffer());
    Util.toConsole("Public key from server received");

    connection.readNBytes(16);
    System.out.println(Arrays.equals(keyAES.getEncoded(), connection.getBuffer()));
  }

  public void decrypt(final byte[] buffer, byte[] b) {

    try {
      AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");

      aesParams.init(b);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keyAES, aesParams);

      byte[] recovered = cipher.doFinal(buffer);

      System.out.println(new String(recovered, 0, recovered.length));
      
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }

    


  }
}