package lx.lindx.talx.server.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lx.lindx.talx.server.Connection;
import lx.lindx.talx.server.Util;

public class Crypt {

  private KeyPair keyPair;
  private SecretKeySpec keyAES;

  // TODO: delete
  byte[] sharedKeySecret;

  public Crypt() {

  }

  public void setClientPubKey(byte[] publicKeyClient) throws GeneralSecurityException { // Generate our public key for
                                                                                        // client

    PublicKey publicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(publicKeyClient));

    DHParameterSpec dhParamFromServer = ((DHPublicKey) publicKey).getParams();

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
    keyPairGenerator.initialize(dhParamFromServer);
    keyPair = keyPairGenerator.generateKeyPair();

    generateSharedKey(publicKey);
  }

  private void generateSharedKey(PublicKey publicKey) {

    try {
      KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(keyPair.getPrivate());
      keyAgree.doPhase(publicKey, true);

      sharedKeySecret = keyAgree.generateSecret();

      keyAES = new SecretKeySpec(sharedKeySecret, 0, 16, "AES");

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public byte[] getSharedKeySecret() {
    return sharedKeySecret;
  }

  public byte[] encrypt(byte[] bytes) {

    ByteBuffer buf = null;

    try {

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keyAES);

      byte[] paramEncoded = cipher.getParameters().getEncoded();
      byte[] cipherMessage = cipher.doFinal(bytes);

      //TODO: DELEte
      System.out.println("p:> " + paramEncoded.length );
      System.out.println("c:> " + cipherMessage.length );

      buf = ByteBuffer.allocate(paramEncoded.length + cipherMessage.length); // 18 + all...
      buf.put(paramEncoded); // 18
      buf.put(cipherMessage); // all....

      return buf.array();

    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public byte[] getPubKeyEncoded() {
    return keyPair.getPublic().getEncoded();
  }
}
