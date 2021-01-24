package Server;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ClienteHandler implements Runnable{
    private Map<String, Utilizador> users;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private Lock lock;
    private Condition condition;
    private String utilizador;


    public ClienteHandler(Map<String, Utilizador> users, Socket socket,Lock lock,Condition c) {
        this.users = users;
        this.socket = socket;
        this.lock = lock;
        this.condition = c;
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
            } catch (EOFException ignored) {

            }
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void comandos(String msg) throws IOException, InterruptedException {
        String[] args = msg.split("/");

        switch (args[0]) {
            case "REGISTAR": //@TODO Mandar mensagem caso utilizador ja exista
                if(comandoRegistar(msg)){
                    out.writeUTF("REGISTERED");
                    out.flush();
                }
                else{
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
                break;
            case "PESSOAS":
                comandoPessoasLocalizacao(msg);
                break;
            case "VAZIO":
                comandoMoverVazio(msg);
                break;
            case "INFETADO":
                comandoInfecao(msg);
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
            this.lock.unlock();
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
            }
            else {
                u.lock();
                if (!u.getPassword().equals(args[2])) {
                    out.writeUTF("NOTVALID");
                    out.flush();
                } else {
                    if (u.isInfetado()) {
                        out.writeUTF("INFETADO");
                        out.flush();
                    }
                    else {
                        this.utilizador = args[1];
                        out.writeUTF("VALID");
                        out.flush();
                    }
                }
                u.unlock();
            }
        }catch (IOException ignored){
        }finally {
            this.lock.unlock();
        }
    }

    public void comandoMover(String msg) throws IOException {
        String[] args = msg.split("/");
        Utilizador u = null;
        Posicao old;
        try {
            this.lock.lock();
            u = this.users.get(this.utilizador);
            u.lock();
            Posicao temp = new Posicao(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            old = u.getPosicao();
            if(!old.equals(temp)) {
                u.setPosicao(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                usersNaZona(u.getPosicao());
                if (posicaoLivre(old)) {
                    condition.signalAll();
                }
                out.writeUTF("MOVED");
                out.flush();
            }
            else {
                out.writeUTF("ALREADY");
                out.flush();
            }
        }finally {
            assert u != null;
            u.unlock();
            this.lock.unlock();
        }

    }

    public boolean posicaoLivre(Posicao posicao) {
        boolean res = true;
        try {
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                u.lock();
            }
            for (Utilizador u : this.users.values()) {
                if (u.getPosicao().equals(posicao)) {
                    res = false;
                }
                u.unlock();
            }
            return res;
        }finally {
            this.lock.unlock();
        }
    }

    public void comandoPessoasLocalizacao(String msg) throws IOException {
        String[] args = msg.split("/");
        int sum = 0;
        try {
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                u.lock();
            }
            for (Utilizador u : this.users.values()) {
                if (u.getPosicao().getPosX() == Integer.parseInt(args[1]) && u.getPosicao().getPosY() == Integer.parseInt(args[2])) {
                    sum++;
                    u.unlock();
                }
            }
            out.writeUTF(String.join("/","PESSOAS",Integer.toString(sum),args[1],args[2]));
            out.flush();
        }finally {
            this.lock.unlock();
        }
    }

    public void comandoMoverVazio(String msg) {
        String[] args = msg.split("/");
        try {
            this.lock.lock();
            Posicao temp = new Posicao(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            if(!this.users.get(this.utilizador).getPosicao().equals(temp)) {
                while (!posicaoLivre(temp)) {
                    this.condition.await();
                }
                out.writeUTF(String.join("/", "VAZIO", args[1], args[2]));
                out.flush();
            }
            else{
                out.writeUTF("ALREADY");
                out.flush();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public void comandoInfecao(String msg) throws IOException {
        try {
            this.lock.lock();
            this.users.get(this.utilizador).infetado();
            /*for (Utilizador u : this.users.values()) {
                u.lock();
            }
            for (Set<String> s : this.users.get(this.utilizador).getContactos().values()) {
                s.forEach(a->this.users.get(a).avisa());
            }
            this.condition.signalAll();*/
            out.writeUTF("INFETADO");
            out.flush();
        }finally {
            /*for (Utilizador u : this.users.values()) {
                u.unlock();
            }*/
            this.lock.unlock();
        }
    }

}
