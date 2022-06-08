package Application;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Server {

  private int port;
  private Map<Socket, Tuple> clients;

  public Server(Integer port) {
    this.port = port;
    clients = new HashMap<Socket, Tuple>();
  }

  public void run() throws IOException {
    ServerSocket server = new ServerSocket(this.port);
    System.out.println("Server running on port " + this.port);

    while (true) {
      Socket client = server.accept();
      System.out.println(client.getInetAddress().getHostAddress() + " connected at " + this.port);

      PrintStream printStream = new PrintStream(client.getOutputStream());

      if (this.clients.containsKey(client)) {
        Tuple tuple = this.clients.get(client);
        tuple.stream = printStream;
        this.clients.replace(client, tuple);
      } else {
        Tuple tuple = new Tuple("Default", printStream);
        this.clients.put(client, tuple);
      }

      ClientHandler handler = new ClientHandler(client, this);
      new Thread(handler).start();
    }
  }

  public void dispatchMessage(String msg, Socket senderClient) throws IOException {
    LocalDateTime now = LocalDateTime.now();
    for (Map.Entry<Socket, Tuple> entry : this.clients.entrySet()) {
      if (entry.getKey() != senderClient) {
        entry.getValue().stream.println(createMessage(entry.getValue().name, msg, now));
        System.out.println(createMessage(entry.getValue().name, msg, now));
      }
    }
  }

  public void logoutMessage(Socket sender){
    LocalDateTime now = LocalDateTime.now();
    for (Map.Entry<Socket, Tuple> entry : this.clients.entrySet()) {
      if (entry.getKey() != sender) {
        entry.getValue().stream.println(createLogoutMessage(entry.getValue().name, now));
        System.out.println(createLogoutMessage(entry.getValue().name, now));
      }
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      throw new IOException("invalid port");
    }

    new Server(Integer.parseInt(args[0])).run();
  }

  public void closeConnection(Socket client) throws IOException {
    System.out.println(this.clients.get(client).name + " disconnected from the server");
    client.close();
  }

  private String createLogoutMessage(String sender, LocalDateTime now){
    StringBuilder sb = new StringBuilder();

    sb.append(sender);
    sb.append(" logged out");
    sb.append(" at ");
    sb.append(formatTime(now).replace(':', 'h'));

    return sb.toString();
  }

  private static String createMessage(String sender, String message, LocalDateTime now) {
    StringBuilder sb = new StringBuilder();

    sb.append(sender);
    sb.append(" says ");
    sb.append(message);
    sb.append(" at ");
    sb.append(formatTime(now).replace(':', 'h'));

    return sb.toString();
  }

  private static String formatTime(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    String formatDateTime = time.format(formatter);
    return formatDateTime;
  }

  public class Tuple{
    public String name;
    public PrintStream stream;

    public Tuple(String name, PrintStream stream){
      this.name = name;
      this.stream = stream;
    }
  }
}
