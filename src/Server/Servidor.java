package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Servidor {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Map<String,Utilizador> users = new HashMap<>();

        while (true) {
            Socket socket = ss.accept();
            Thread worker = new Thread(new ClienteHandler(users,socket));
            worker.start();
        }
    }

}