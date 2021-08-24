package lx.lindx.talx.client.security;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

/**
 * Crypt module
 */
public class Crypt {

  private KeyAgreement keyAgree;

  private byte[] pubkeyEncoded;
  
  private SecretKeySpec keyAES;

  public Crypt() {

    try {

      KeyPair keyPair = KeyPairGenerator.getInstance("DH").generateKeyPair();

      keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(keyPair.getPrivate());
      
      pubkeyEncoded = keyPair.getPublic().getEncoded();
    } catch (

    GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  // Publick key для отправки на сервер
  public byte[] getPublicKeyEncoded() {
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

  public SecretKeySpec getKeyAES() {
    return keyAES;
  }
}