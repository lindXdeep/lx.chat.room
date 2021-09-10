package lx.talx.client.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

  private KeyAgreement keyAgree;
  private byte[] pubkeyEncoded;
  private SecretKeySpec keyAES;

  public void generatePublicKey() {

    try {

      KeyPair keyPair = KeyPairGenerator.getInstance("DH").generateKeyPair();

      keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(keyPair.getPrivate());

      pubkeyEncoded = keyPair.getPublic().getEncoded();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public byte[] getPubKeyEncoded() {
    return pubkeyEncoded;
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

  public byte[] encrypt(byte[] bytes) {
    
    ByteBuffer buf;

    try {

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keyAES);

      byte[] cipherMsg = cipher.doFinal(bytes);
      byte[] encodeParam = cipher.getParameters().getEncoded();

      buf = ByteBuffer.allocate(encodeParam.length + cipherMsg.length); // 18 + all...
      buf.put(encodeParam);
      buf.put(cipherMsg);

      return buf.array();

    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException();

  }

  public byte[] decrypt(byte[] encodeSpec, byte[] cipherMsg) {

    try {
      AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");

      aesParams.init(encodeSpec);
   
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keyAES, aesParams);

      return cipher.doFinal(cipherMsg);

    } catch (GeneralSecurityException | IOException e) {
      System.out.println("Can't decrypt");
    }
    throw new RuntimeException("Lost connection to server");
  }
}
