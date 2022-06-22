import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


//Para adotar todos os comprtamentos dessa classe
public class Servidor extends Thread {

  //Atributos Estáticos
  private static ArrayList<BufferedWriter>clientes;
  private static ServerSocket server;


  //Atributos 
  private String nome;
  private Socket con;
  private InputStream in;
  private InputStreamReader inr;
  private BufferedReader bfr;

  /**
    * Método construtor
    * @param con do tipo Socket
    */
  public Servidor(Socket con){
    this.con = con;
    try {
          in  = con.getInputStream();
          inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
    } catch (IOException e) {
            e.printStackTrace();
    }
  }//end método construtor

  /**
    * Método run
    */
  public void run(){

    try{
      String msg;
      OutputStream ou =  this.con.getOutputStream();
      Writer ouw = new OutputStreamWriter(ou);
      BufferedWriter bfw = new BufferedWriter(ouw);
      clientes.add(bfw);
      nome = msg = bfr.readLine();

      while(!"Desconectado".equalsIgnoreCase(msg) && msg != null)
        {
        msg = bfr.readLine();
        sendToAll(bfw, msg);
        }

    }catch (Exception e) {
      e.printStackTrace();

    }
  }//end método run



  /***
   * Método usado para enviar mensagem para todos os clients
   * @param bwSaida do tipo BufferedWriter
   * @param msg do tipo String
   * @throws IOException
   */
  public void sendToAll(BufferedWriter bwSaida, String msg) throws  IOException
  {
    BufferedWriter bwS;

    for(BufferedWriter bw : clientes){
      bwS = (BufferedWriter)bw;
      if(!(bwSaida == bwS)){
        bw.write(nome + " -> " + msg+"\r\n");
        bw.flush();
      }
    }
  }//end método sendToAll




  /***
     * Método main
     * @param args
     */
  public static void main(String []args) {
    int porta = 12345; //porta 

    try{
      if(args.length != 1)
        throw new Exception("ERROR - Usage: java Servidor <(int) Porta>");
      porta = Integer.parseInt(args[0]);

      //Cria os objetos necessário para instânciar o servidor
      server = new ServerSocket(porta);
      clientes = new ArrayList<BufferedWriter>();
      System.out.println("Servidor ativo na porta: " + porta);

      while(true){
        System.out.println("Aguardando conexão...");
        Socket con = server.accept();
        System.out.println("Cliente conectado...");
        Thread t = new Servidor(con);
          t.start();
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }// Fim do método main
} //Fim da classe