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
    public void run() {
        try {
            this.in = new DataInputStream(socket.getInputStream());
            String input;
            while ((input = this.in.readUTF())!= null){ //Logout
                //System.out.println(input);
                if(s.isWaiting())
                    s.stopWaiting();
                if(input.equals("VALID"))
                    log.login();
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
                if (input.equals("SAIR")) {
                    log.sair();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
