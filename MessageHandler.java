package Application;

import java.io.InputStream;
import java.util.Scanner;

public class MessageHandler implements Runnable {

  private InputStream server;

  public MessageHandler(InputStream server) {
      this.server = server;
  }

  public void run() {
      Scanner scanner = new Scanner(this.server);
      while (scanner.hasNextLine()) {
          System.out.println(scanner.nextLine());
      }
  }
}

