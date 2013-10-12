//package com.eceapp.sasel;

import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class CipherAlgo {
  public final String ALGO_NAME = "AES"; 
  public final String ALGO_NAME_SPECIFICATION = "AES/CBC/PKCS5Padding";

  //EncryptionKey and Initialization Vector must be 16bytes long
  private String encryptionKey, Init_Vect;

  public static final int REQ_IV_LEN = 16;
  public static final int REQ_ENCRYPTION_LEN = 16; 

  private Cipher cipher;
  
  public static void main(String[] args) {
    System.out.println("Cipher class initiated and main called successfully");
  }

  public CipherAlgo(String enc_Key, String iv) {
    //Constructor for the AES based cipher object
    try {
      cipher = Cipher.getInstance(ALGO_NAME_SPECIFICATION);
      Init_Vect = iv;
      encryptionKey = enc_Key;
    } catch(NoSuchAlgorithmException e) {
      String errMsg = String.format(
        "No such algorithm %s exists\nException being raised", 
        ALGO_NAME_SPECIFICATION
      );
      System.err.println(errMsg);
      e.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    if (encryptionKey.length() != REQ_ENCRYPTION_LEN) {
      String errMsg = String.format(
        "Expecting an encryption key of %d bytes.", REQ_ENCRYPTION_LEN 
      );
      System.err.println(errMsg);
      return; //System.exit(-1);
    }

    if (iv.length() != REQ_IV_LEN) {
      System.err.println("Expecting an initialization vector of " + 
      REQ_IV_LEN + " bytes.\nExiting now...");
      return;//System.exit(-1);
    }
  }

  public byte[] encrypt(final String plainText) throws Exception {
    //Switches cipher to encryption mode and 
    //returns an array of bytes containing the encrypted text
    SecretKeySpec key = new SecretKeySpec(
      encryptionKey.getBytes("UTF-8"), ALGO_NAME
    );

    cipher.init(
      Cipher.ENCRYPT_MODE, key, 
      new IvParameterSpec(Init_Vect.getBytes("UTF-8"))
    );

    return cipher.doFinal(plainText.getBytes("UTF-8"));
  }

  public static void print() {
    //Test method to for initialization
    System.out.println("Print method called");
  }

  public byte[] decrypt(byte[] cipherText) throws Exception {
    //Should return a byte array due to the paranoia of String immutability
    //hindering erasal until Garbage Collection  kicks in
    SecretKeySpec key = new SecretKeySpec(
      encryptionKey.getBytes("UTF-8"), ALGO_NAME
    );

    cipher.init(
      Cipher.DECRYPT_MODE, key, 
      new IvParameterSpec(Init_Vect.getBytes("UTF-8"))
    );

    return cipher.doFinal(cipherText);
  }

  public boolean setIVKey(String newIVKey) {
    /*
      Returns true iff the IV key was modified, 
      and matches the specified length
    */
    boolean changeMadeBool = false;
    if (newIVKey.length() == REQ_IV_LEN && (! Init_Vect.equals(newIVKey))) {
      this.Init_Vect = newIVKey;
      changeMadeBool = true;
    }

    return changeMadeBool;
  }

  public boolean setEncryptionKey(String newEncryptionKey) {
    /*
      Returns true iff the Encryption key was modified, 
      and matches the specified length
    */
    boolean changeMadeBool = false;
    boolean keyCanBeChanged =  
         (newEncryptionKey.length() == REQ_ENCRYPTION_LEN) 
      && (! encryptionKey.equals(newEncryptionKey));

    if (keyCanBeChanged) {
      this.encryptionKey = newEncryptionKey;
      changeMadeBool = true;
    }

    return changeMadeBool;
  }
}
