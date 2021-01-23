package Server;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class ClienteHandler implements Runnable{
    private Map<String, Utilizador> users;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private Lock lock;
    private String utilizador;

    public ClienteHandler(Map<String, Utilizador> users, Socket socket,Lock lock) {
        this.users = users;
        this.socket = socket;
        this.lock = lock;
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
                if(comandoRegistar(msg)){
                    System.out.println(this.users);
                    out.writeUTF("REGISTERED");
                    out.flush();
                }
                else{
                    System.out.println(this.users);
                    out.writeUTF("NOTREGISTERED");
                    out.flush();
                }
                break;
            case "LOGIN":
                comandoLogin(msg);
                break;
            case "LOGOUT":
                out.writeUTF("LOGOUT");
                out.flush();
                this.utilizador = null;
                break;
            case "MOVER":
                comandoMover(msg);
                System.out.println(this.users);
                break;
            case "SAIR":
                out.writeUTF("SAIR");
                out.flush();
                break;
        }
    }

    public boolean comandoRegistar(String msg) {
        String[] args = msg.split("/");
        Utilizador u = new Utilizador(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        try {
            this.lock.lock();
            if(!this.users.containsKey(u.getUsername())) {
                this.users.put(u.getUsername(), u);
                this.usersNaZona(u.getPosicao());
                return true;
            }
            else return false;
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
                    Set<String> contactos = new HashSet<>();
                    for (Utilizador ut : this.users.values()) {
                        if (!ut.getUsername().equals(us.getUsername())) {
                            if (ut.getPosicao().equals(us.getPosicao())) {
                                contactos.add(ut.getUsername());
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
        try {
            this.lock.lock();
            Utilizador u = this.users.get(args[1]);
            if (u == null ) {
                out.writeUTF("NOTVALID");
                out.flush();
            } else if (!u.getPassword().equals(args[2])) {
                out.writeUTF("NOTVALID");
                out.flush();
            }else {
                out.writeUTF("VALID");
                out.flush();
                this.utilizador = args[1];
            }
        }catch (IOException ignored){
        }finally {
            this.lock.unlock();
        }
    }

    public void comandoMover(String msg) throws IOException {
        String[] args = msg.split("/");
        Utilizador u = null;
        try {
            this.lock.lock();
            u = this.users.get(this.utilizador);
            u.lock();
            u.setPosicao(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            usersNaZona(u.getPosicao());
        }finally {
            assert u != null;
            u.unlock();
            this.lock.unlock();
        }

        out.writeUTF("MOVED");
        out.flush();
    }

}
