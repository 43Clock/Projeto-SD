package Server;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Utilizador {
    private String username;
    private String password;
    private Lock lock;
    private Posicao posicao;
    private Map<Posicao,Set<String>> contactos;
    private boolean infetado;
    private boolean isolamento;
    private boolean notificado;


    public Utilizador(String username, String password, int x, int y) {
        this.username = username;
        this.password = password;
        this.posicao = new Posicao(x, y);
        this.contactos = new HashMap<>();
        this.lock = new ReentrantLock();
        this.infetado = false;
        this.notificado = false;
        this.isolamento = false;
    }

    public String getUsername() {
        try {
            this.lock.lock();
            return username;
        } finally {
            lock.unlock();
        }
    }

    public String getPassword() {
        try {
            this.lock.lock();
            return password;
        } finally {
            this.lock.unlock();
        }
    }

    public Posicao getPosicao() {
        try {
            this.lock.lock();
            return posicao;
        } finally {
            this.lock.unlock();
        }
    }

    public Map<Posicao, Set<String>> getContactos() {
        try {
            this.lock.lock();
            return contactos;
        } finally {
            this.lock.unlock();
        }
    }

    public void setPosicao(int x, int y) {
        try {
            this.lock.lock();
            if(!this.temPosicao(x,y))
                this.posicao = new Posicao(x,y);
            else{
                this.posicao = this.contactos.keySet().stream().filter(a -> a.getPosX() == x && a.getPosY() == y).collect(Collectors.toList()).get(0);
            }
        }finally {
            this.lock.unlock();
        }
    }

    public boolean temPosicao(int x, int y) {
        return this.contactos.keySet().stream().anyMatch(a -> a.getPosX() == x && a.getPosY() == y);
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public void alteraContactos(Set<String> contactos) {
        try {
            this.lock.lock();
            this.contactos.put(this.posicao,contactos);
        }finally {
            this.lock.unlock();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Utilizador{");
        sb.append("username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", posicao=").append(posicao);
        sb.append(", contactos=").append("{");
        for (Map.Entry<Posicao, Set<String>> s : this.contactos.entrySet()) {
            sb.append(s.getKey().toString()).append("->{");
            s.getValue().forEach(a->sb.append(a).append(","));
            sb.append("}");
        }
        sb.append("}, infetado=").append(infetado);
        sb.append(", isolamento=").append(isolamento);
        sb.append(", notificado=").append(notificado);
        sb.append("}\n");
        return sb.toString();
    }
}
