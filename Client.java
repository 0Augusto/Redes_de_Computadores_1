package Application;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

  private String host;
  private int port;
  private boolean isConnected;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
    this.isConnected = true;
  }

  public void run() throws UnknownHostException, IOException {
    Socket client = new Socket(this.host, this.port);
    System.out.println("Connected successful on " + this.host + ":" + this.port);

    MessageHandler handler = new MessageHandler(client.getInputStream());
    new Thread(handler).start();

    Scanner scanner = new Scanner(System.in);
    PrintStream printStream = new PrintStream(client.getOutputStream());

    while (this.isConnected && scanner.hasNextLine()) {
      String input = scanner.nextLine();

      if (verifyQuit(input)){
        printStream.println(input);
        closeConnection(printStream, scanner, client);
      }
      printStream.println(input);
    }
  }

  private boolean verifyQuit(String content) {
    return content.contains("sair");
  }

  public static void main(String[] args) throws UnknownHostException, IOException {

    if (args.length < 2) {
      throw new IOException("Unsuficient parameters");
    }
    new Client(args[0], Integer.parseInt(args[1])).run();
  }

  public void closeConnection(PrintStream printStream, Scanner scanner, Socket client) throws IOException {
    System.out.println("Disconnected from the server");
    printStream.close();
    client.close();
    this.isConnected = false;
  }
}
