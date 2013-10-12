import java.util.Date;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class CipherApply extends Object {
  public final int FILE_NULLITY = 0;

  private String filePath;  

  private String encryptionKey;
  private String initializationVector;

  private String outFile;

  private CipherAlgo theCipherAlgo;

  public CipherApply() {
    this.filePath = this.initializationVector = this.encryptionKey = null;
    this.theCipherAlgo = null;
  }

  public void setFilePath(String newPath) {
    this.filePath = newPath;
  }

  public boolean instantiateCipher(String encryptionKey, String ivKey) {
    this.theCipherAlgo = new CipherAlgo(encryptionKey, ivKey);
    return false;
  }

  public String getFilePath() {
    return this.filePath;
  }

  public String readFromFile(String filePath) {
    String outData = null;
    String lineIn = null;

    try {
      BufferedReader rIn = new BufferedReader(new FileReader(filePath));
      outData = "";

      while (true) {
	lineIn = rIn.readLine();
	if (lineIn == null) break;
	outData += lineIn;
      }

      rIn.close();
    } catch (Exception e) {
      ;
    }

    return outData;
  }

  public boolean setEncryptionKey(String newEncryptionKey) {
    this.encryptionKey = newEncryptionKey;
    return this.theCipherAlgo.setEncryptionKey(this.encryptionKey);
  }

  public boolean setIVKey(String newIVKey) {
    this.initializationVector = newIVKey;
    return this.theCipherAlgo.setIVKey(this.initializationVector);
  }

  public String getEncryptionKey() {
    return this.encryptionKey;
  }

  public static void main() {
    System.out.println("Still sane");
  }

  @Override
  public String toString() {
    return String.format("CipherApply:xxxx");
  }

  public int dumpDecryption(String outFile) throws IOException {
    //Write to memory the decrypted data, returning the number of bytes written
    if (outFile == null) 
      return FILE_NULLITY;

    Date dateTimeStamp = new Date();
  
    int writtenBytes = -1;

    try {
      writtenBytes = 1;
      BufferedWriter outWriter = new BufferedWriter(
	new FileWriter(outFile)
      );

      outWriter.write(dateTimeStamp.toString());
      outWriter.close();

    } catch (IOException ioex) {
      ioex.printStackTrace();

    } catch (Exception genericex) {
      genericex.printStackTrace();
    }

    return writtenBytes;
  }
}
