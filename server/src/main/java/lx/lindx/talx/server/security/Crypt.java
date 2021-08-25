package lx.lindx.talx.server.security;

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
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

  private KeyPair keyPair;
  private SecretKeySpec keyAES;

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

      byte[] sharedKeySecret = keyAgree.generateSecret();

      keyAES = new SecretKeySpec(sharedKeySecret, 0, 16, "AES");

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public byte[] encrypt(byte[] bytes) {

    ByteBuffer buf = null;

    try {

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keyAES);

      byte[] paramEncoded = cipher.getParameters().getEncoded();
      byte[] cipherMessage = cipher.doFinal(bytes);

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

  public byte[] decrypt(final byte[] encodeParam, final byte[] buffer) {

    try {
      AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");

      aesParams.init(encodeParam);

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keyAES, aesParams);

      return cipher.doFinal(buffer);

    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }

    throw new RuntimeException();
  }
}
