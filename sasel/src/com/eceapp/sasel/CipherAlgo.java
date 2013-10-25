package com.eceapp.sasel;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherAlgo {
  public final String ALGO_NAME = "AES"; 
  public final String ALGO_NAME_SPECIFICATION = "AES/CBC/PKCS5Padding";

  //EncryptionKey and Initialization Vector must be 16bytes long
  private String encryptionKey, initVect;

  public static final int REQ_IV_LEN = 16;
  public static final int REQ_ENCRYPTION_LEN = 16; 

  private Cipher cipher;
  
  public static void main(String[] args) {
    System.out.println("Cipher class initiated and main called successfully");
  }

  public CipherAlgo(String enc_Key, String iv) {
    //Constructor for the AES based cipher object
    try {
      this.cipher = Cipher.getInstance(ALGO_NAME_SPECIFICATION);
      this.initVect = iv;
      this.encryptionKey = enc_Key;
    } catch(NoSuchAlgorithmException e) {
      String errMsg = String.format(
        Locale.CANADA, 
        "No such algorithm %s exists\nException being raised", ALGO_NAME_SPECIFICATION
      );
      
      System.err.println(errMsg);
      e.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    if (this.encryptionKey.length() != REQ_ENCRYPTION_LEN) {
      String errMsg = String.format(
        Locale.CANADA, "Expecting an encryption key of %d bytes.", REQ_ENCRYPTION_LEN 
      );
      System.err.println(errMsg);
      return;
    }

    if (iv.length() != REQ_IV_LEN) {
      System.err.println(
    	 "Expecting an initialization vector of " + REQ_IV_LEN + " bytes.\nExiting now..."
      );
      return;
    }
  }

  public byte[] encrypt(final String plainText) throws Exception {
    //Switches cipher to encryption mode and 
    //returns an array of bytes containing the encrypted text
    SecretKeySpec key = new SecretKeySpec(
      this.encryptionKey.getBytes("UTF-8"), ALGO_NAME
    );

    this.cipher.init(
      Cipher.ENCRYPT_MODE, key, 
      new IvParameterSpec(this.initVect.getBytes("UTF-8"))
    );

    return this.cipher.doFinal(plainText.getBytes("UTF-8"));
  }

  public static void print() {
    //Test method to for initialization
    System.out.println("Print method called");
  }

  public byte[] decrypt(byte[] cipherText) throws Exception {
    //Should return a byte array due to the paranoia of String immutability
    //hindering erasal until Garbage Collection  kicks in
    SecretKeySpec key = new SecretKeySpec(
      this.encryptionKey.getBytes("UTF-8"), ALGO_NAME
    );

    this.cipher.init(
      Cipher.DECRYPT_MODE, key, 
      new IvParameterSpec(initVect.getBytes("UTF-8"))
    );

    return this.cipher.doFinal(cipherText);
  }

  public boolean setIVKey(String newIVKey) {
    /*
      Returns true iff the IV key was modified, 
      and matches the specified length
    */
    boolean changeMadeBool = false;
    if (newIVKey.length() == REQ_IV_LEN && (! initVect.equals(newIVKey))) {
      this.initVect = newIVKey;
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
      && (! this.encryptionKey.equals(newEncryptionKey));

    if (keyCanBeChanged) {
      this.encryptionKey = newEncryptionKey;
      changeMadeBool = true;
    }

    return changeMadeBool;
  }
}
