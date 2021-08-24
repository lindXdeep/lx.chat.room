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

public class Crypt {

  private KeyPair keyPair;

  private SecretKeySpec keyAES;

  public Crypt(final byte[] publicKeyClient) { // get Server Pub key Encoded

    // Generate our public key for client
    try {

      PublicKey publicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(publicKeyClient));

      DHParameterSpec dhParamFromServer = ((DHPublicKey) publicKey).getParams();

      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
      keyPairGenerator.initialize(dhParamFromServer);
      keyPair = keyPairGenerator.generateKeyPair();

      generateSharedKey(publicKey);

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public Crypt(Connectrion connectrion) {
  }

  public byte[] getPublicKeyEncoded() {
    return keyPair.getPublic().getEncoded();
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
  
  public SecretKeySpec getKeyAES() {
    return keyAES;
  }
}
