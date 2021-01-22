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
                System.out.println(input);
                if(s.isWaiting())
                    s.stopWaiting();
                if(input.equals("TRUE"))
                    log.login();
                if(input.equals("LOGOUT")) {
                    log.logout();
                }
                if (input.equals("SAIR")) {
                    log.sair();
                    /*socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();*/
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
