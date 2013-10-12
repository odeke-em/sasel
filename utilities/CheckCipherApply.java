import java.io.IOException;
public class CheckCipherApply {
  public static void main(String[] args) {
    CipherApply cipherApp = new CipherApply();

    System.out.println(cipherApp);
    try {
      cipherApp.dumpDecryption("lampoza");
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }

    String inData = cipherApp.readFromFile("CheckCipherApply.java");
    System.out.println(inData);
  }
}
