package Application;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

  private InputStream client;
  private Server server;
  private Socket socket;

  public ClientHandler(Socket client, Server server) throws IOException {
    this.client = client.getInputStream();
    this.server = server;
    this.socket = client;
  }

  public void run() {
    // quando chegar uma msg, distribui pra todos
    Scanner scanner = new Scanner(this.client);
    while (scanner.hasNextLine()) {
      try {
        String input = scanner.nextLine();
        if (verifyQuit(input)) {
          server.closeConnection(this.socket);
          server.logoutMessage(this.socket);
        } else {
          server.dispatchMessage(input, this.socket);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    scanner.close();
  }

  private boolean verifyQuit(String content) {
    return content.contains("sair");
  }
}
