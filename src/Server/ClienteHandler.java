package Server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClienteHandler implements Runnable{
    private Map<String, Utilizador> users;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private Lock lock;



    public ClienteHandler(Map<String, Utilizador> users, Socket socket) {
        this.users = users;
        this.socket = socket;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        try {
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            String line;
            try {
                while ((line = in.readUTF()) != null) {
                    comandos(line);
                }//logout
            } catch (EOFException e) {

            }
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void comandos(String msg) throws IOException {
        String[] args = msg.split("/");

        switch (args[0]) {
            case "REGISTAR": //@TODO Mandar mensagem caso utilizador ja exista
                comandoRegistar(msg);
                System.out.println(this.users);
                out.writeUTF("Utilizador Registado");
                out.flush();
                break;
            case "LOGIN":
                comandoLogin(msg);
                break;
            case "LOGOUT":
                out.writeUTF("LOGOUT");
                out.flush();
                break;
            case "SAIR":
                out.writeUTF("SAIR");
                out.flush();
                break;
        }
    }

    public void comandoRegistar(String msg) {
        String[] args = msg.split("/");
        Utilizador u = new Utilizador(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4])); //@TODO parse Int e Utilizador DIfernete
        try {
            this.lock.lock();
            this.users.put(u.getUsername(), u);
            this.usersNaZona(u.getPosicao());

        }finally {
            this.lock.unlock();
        }
    }

    public void usersNaZona(Posicao p) {
        try {
            this.lock.lock();
            for (Utilizador ut : this.users.values()) {
                ut.lock();
            }
        }finally {
            this.lock.unlock();
        }
        try {
            for (Utilizador us:this.users.values()){
                if(us.getPosicao().equals(p)){
                    Set<Utilizador> contactos = new HashSet<>();
                    for (Utilizador ut : this.users.values()) {
                        if (!ut.getUsername().equals(us.getUsername())) {
                            if (ut.getPosicao().equals(us.getPosicao())) {
                                contactos.add(ut);
                            }
                        }
                    }
                us.alteraContactos(contactos);
                }
            }
        }finally {
            for (Utilizador ut : this.users.values()) {
                ut.unlock();
            }
        }
    }

    public void comandoLogin(String msg)  {
        String[] args = msg.split("/");
        Utilizador u = this.users.get(args[1]);
        try {
            if (u == null ) {
                out.writeUTF("FALSE");
                out.flush();
            } else if (!u.getPassword().equals(args[2])) {
                out.writeUTF("FALSE");
                out.flush();
            }else {
                out.writeUTF("TRUE");
                out.flush();
            }
        }catch (IOException e){
        }
    }

}
