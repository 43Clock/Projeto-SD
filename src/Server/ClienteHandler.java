package Server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ClienteHandler implements Runnable{
    private Map<String, Utilizador> users;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private Lock lock;
    private Condition condition;
    private Condition conditionCovid;
    private String utilizador;
    private Thread covid;


    public ClienteHandler(Map<String, Utilizador> users, Socket socket,Lock lock,Condition c,Condition c2) {
        this.users = users;
        this.socket = socket;
        this.lock = lock;
        this.condition = c;
        this.conditionCovid = c2;
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
            case "REGISTAR":
                comandoRegistar(msg);
                break;
            case "LOGIN":
                comandoLogin(msg);
                break;
            case "LOGOUT":
                comandoLogout(msg);
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
            case "MAPA":
                comandoMapa(msg);
                break;
            case "SAIR":
                out.writeUTF("SAIR");
                out.flush();
                break;
        }
    }

    public void comandoLogout(String msg) throws IOException {
        try {
            this.lock.lock();
            out.writeUTF("LOGOUT");
            out.flush();
            this.utilizador = null;
            this.covid.interrupt();
        }finally {
            this.lock.unlock();
        }
    }

    public void comandoRegistar(String msg) {
        String[] args = msg.split("/");
        Utilizador u = new Utilizador(args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        try {
            this.lock.lock();
            if (!this.users.containsKey(u.getUsername())) {
                this.users.put(u.getUsername(), u);
                this.usersNaZona(u.getPosicao());
                out.writeUTF("REGISTERED");
            } else {
                out.writeUTF("NOTREGISTERED");
            }
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public void usersNaZona(Posicao p) {
        try {
            this.lock.lock();
            Collection<Utilizador> users = new ArrayList<>();
            for (Utilizador ut : this.users.values()) {
                users.add(ut);
                ut.lock();
            }
            this.lock.unlock();
            for (Utilizador us: users){
                if(us.getPosicao().equals(p)){
                    Set<String> contactos = new HashSet<>();
                    for (Utilizador ut : users) {
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
                        this.covid = new Thread(new CovidHandler(this.out, this.lock, this.conditionCovid, this.users.get(this.utilizador)));
                        this.covid.start();
                        if (!u.isEspecial()) {
                            out.writeUTF("VALID");
                        } else {
                            out.writeUTF("VALIDESPECIAL");
                        }
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
        Utilizador u;
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
            u.unlock();
        }finally {
            this.lock.unlock();
        }

    }

    public boolean posicaoLivre(Posicao posicao) {
        boolean res = true;
        try {
            Collection<Utilizador> users = new ArrayList<>();
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                users.add(u);
                u.lock();
            }
            this.lock.unlock();
            for (Utilizador u : users) {
                if (u.getPosicao().equals(posicao)) {
                    res = false;
                }
                u.unlock();
            }
        }catch(Exception ignored) {}
        return res;
    }

    public void comandoPessoasLocalizacao(String msg) {
        String[] args = msg.split("/");
        int sum = 0;
        try {
            Collection<Utilizador> users = new ArrayList<>();
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                users.add(u);
                u.lock();
            }
            this.lock.unlock();
            for (Utilizador u : users) {
                if (u.getPosicao().getPosX() == Integer.parseInt(args[1]) && u.getPosicao().getPosY() == Integer.parseInt(args[2])) {
                    sum++;
                }
                u.unlock();
            }
            out.writeUTF(String.join("/","PESSOAS",Integer.toString(sum),args[1],args[2]));
            out.flush();
        }catch (IOException ignored){}
    }

    public void comandoMoverVazio(String msg) {
        String[] args = msg.split("/");
        try {
            this.lock.lock();
            Posicao temp = new Posicao(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            if(!this.users.get(this.utilizador).getPosicao().equals(temp)) {
                Thread t = new Thread(new EsperaHandler(out, lock, condition, temp, users));
                t.start();
                out.writeUTF("");
                out.flush();
            }
            else{
                out.writeUTF("ALREADY");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public void comandoInfecao(String msg) throws IOException {
        try {
            this.lock.lock();
            this.users.get(this.utilizador).infetado();
            for (Utilizador u : this.users.values()) {
                u.lock();
            }
            for (Set<String> s : this.users.get(this.utilizador).getContactos().values()) {
                s.forEach(a->this.users.get(a).avisa());
            }
            this.conditionCovid.signalAll();
            this.covid.interrupt();
            out.writeUTF("INFETADO");
            out.flush();
        }finally {
            for (Utilizador u : this.users.values()) {
                u.unlock();
            }
            this.lock.unlock();
        }
    }

    public void comandoMapa(String msg) {
        try {
            this.lock.lock();
            int x = 0, y = 0;
            for (Utilizador u : this.users.values()) {
                u.lock();
            }
            for(Utilizador u : this.users.values()){ //Dimensao do mapa
                for (Posicao p : u.getContactos().keySet()) {
                    x = Math.max(p.getPosX(),x);
                    y = Math.max(p.getPosY(),y);
                }
                u.unlock();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("MAPA/").append(x).append("/").append(y);
            for (int i = 0; i <=x; i++) {
                for (int j = 0; j <=y; j++) {
                    sb.append("/").append(usersNaPosicao(i, j)).append("-").append(userInfetadosPosicao(i,j));
                }
            }
            String s = sb.toString();
            out.writeUTF(s);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public int usersNaPosicao(int x, int y) {
        int r = 0;
        try {
            Collection<Utilizador> users = new ArrayList<>();
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                users.add(u);
                u.lock();
            }
            this.lock.unlock();
            for (Utilizador u : users) {
                for (Posicao p : u.getContactos().keySet()) {
                    if (p.getPosX() == x && p.getPosY() == y) {
                        r++;
                        break;
                    }
                }
                u.unlock();
            }
        } catch (Exception ignored) {}
        return r;
    }

    public int userInfetadosPosicao(int x,int y) {
        int r = 0;
        try {
            Collection<Utilizador> users = new ArrayList<>();
            this.lock.lock();
            for (Utilizador u : this.users.values()) {
                users.add(u);
                u.lock();
            }
            this.lock.unlock();
            for (Utilizador u : users) {
                if(u.isInfetado()){
                    for (Posicao p : u.getContactos().keySet()) {
                        if (p.getPosX() == x && p.getPosY() == y) {
                            r++;
                            break;
                        }
                    }
                }
                u.unlock();
            }
        } catch (Exception ignored) {}
        return r;
    }
}
