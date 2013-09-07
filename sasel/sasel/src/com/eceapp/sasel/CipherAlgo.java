package com.eceapp.sasel;

import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class CipherAlgo{
  public final String ALGO_NAME_SPECIFICATION = "AES/CBC/PKCS5Padding",
			ALGO_NAME = "AES"; 

  //EncryptionKey and Initialization Vector must be 16bytes long
  private String encryptionKey, Init_Vect;

  public static final int REQ_ENCRYPTION_LEN = 16, REQ_IV_LEN = 16;

  private Cipher cipher;
  
  public static void main(String[] args){
    System.out.println( "Cipher class initiated and main called successfully" );
  }

  public CipherAlgo(String enc_Key, String iv){
    //Constructor for the AES based cipher object
    try{
       cipher	    = Cipher.getInstance( ALGO_NAME_SPECIFICATION );
      encryptionKey = enc_Key;
      Init_Vect	    = iv;
    } catch( NoSuchAlgorithmException e ){
      System.err.println( "No such Algorithm "+ 
	  ALGO_NAME_SPECIFICATION+"exists\nException being raised" );
      e.printStackTrace();
    }catch( Exception e ){
      e.printStackTrace();
    }
    
    if (encryptionKey.length() != REQ_ENCRYPTION_LEN ){
      System.err.println("Expecting an encryption key  of " + 
		  REQ_ENCRYPTION_LEN + " bytes.\nExiting now...");
      return; //System.exit( -1 );
    }

    if (iv.length() != REQ_IV_LEN){
      System.err.println( "Expecting an initialization vector of " + 
		  REQ_IV_LEN + " bytes.\nExiting now..." );
      return;//System.exit(-1);
    }
  }

  public byte[] encrypt(final String plainText) throws Exception {
    //Switches cipher to encryption mode and 
    //returns an array of bytes containing the encrypted text
    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"),
					    ALGO_NAME);

    cipher.init(Cipher.ENCRYPT_MODE, key, 
    		new IvParameterSpec(Init_Vect.getBytes("UTF-8")));

    return cipher.doFinal( plainText.getBytes( "UTF-8" ));
  }

  public static void print()
  { 
    //Test method to for initialization
    System.out.println( "Print method called" );
  }

  public byte[] decrypt(byte[] cipherText) throws Exception{
    //Should return a byte array due to the paranoia of String immutability
    //hindering erasal until Garbage Collection  kicks in
    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"),
					    ALGO_NAME);
    cipher.init(Cipher.DECRYPT_MODE, key, 
    		new IvParameterSpec(Init_Vect.getBytes("UTF-8")));

    return cipher.doFinal( cipherText );
  }
  public boolean setIVKey(String newIVKey){
	  /*Returns true iff the IV key was modified, and matches the specified length*/
	  boolean changeMadeBool = false;
	  if ((newIVKey.length() == REQ_IV_LEN) && (! Init_Vect.equals(newIVKey))){
		  Init_Vect = newIVKey;
		  changeMadeBool = true;
	  }
	  return changeMadeBool;
  }
  public boolean setEncryptionKey(String newEncryptionKey){
	  /*Returns true iff the Encryption key was modified, and matches the specified length*/
	  boolean changeMadeBool = false;
	  if ((newEncryptionKey.length() == REQ_ENCRYPTION_LEN) && 
			  (! encryptionKey.equals(newEncryptionKey))){
		  encryptionKey = newEncryptionKey;
		  changeMadeBool = true;
	  }
	  return changeMadeBool;
  }
}
