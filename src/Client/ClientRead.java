package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

public class ClientRead implements Runnable{
    private Socket socket;
    private DataInputStream in;
    private Log log;
    private Syncronizer s;

    public ClientRead(Socket socket, Log log, Syncronizer s) {
        this.socket = socket;
        this.log = log;
        this.s = s;
    }

    @Override
    public void run() { //@TODO MUDAR PARA SWITCH
        try {
            this.in = new DataInputStream(socket.getInputStream());
            String input;
            while ((input = this.in.readUTF())!= null){ //Logout
                String args[] = input.split("/");
                if(input.equals("VALID")) {
                    log.login();
                }
                if (input.equals("NOTVALID")) {
                    System.out.println("\nUtilizador ou Password errados! ");
                }
                if(input.equals("LOGOUT")) {
                    log.logout();
                }
                if (input.equals("REGISTERED")) {
                    System.out.println("\nUtilizador Registado com sucesso!");
                }
                if (input.equals("NOTREGISTERED")) {
                    System.out.println("\nUtilizador já registado!");
                }
                if (input.equals("MOVED")) {
                    System.out.println("\nLocalização Atualizada");
                }
                if(args[0].equals("PESSOAS")){
                    System.out.println("\nExistem "+args[1]+" pessoas na localização ("+args[2]+","+args[3]+")");
                }
                if (args[0].equals("VAZIO")) {
                    System.out.println("\nA posição ("+args[1]+","+args[2]+") encontra-se agora vazia.");
                }
                if (input.equals("ALREADY")) {
                    System.out.println("\nJá se encontra nessa posição");
                }
                if (input.equals("INFETADO")) {
                    System.out.println("\nInteração com o servidor bloqueada porque se encontra infetado");
                    log.logout();
                }
                if (input.equals("AVISO")) {
                    System.out.println("\nEsteve em contacto com um Infetado, não seja como as machas");
                }
                if (input.equals("SAIR")) {
                    log.sair();
                    break;
                }
                if(s.isWaiting())
                    s.stopWaiting();
            }
        } catch (IOException ignored) {

        }
    }
}
