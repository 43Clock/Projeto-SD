package Server;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    private String username;
    private String password;
    private Lock lock;
    private Posicao posicao;
    private Map<Posicao,Set<Utilizador>> contactos;
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

    public Map<Posicao, Set<Utilizador>> getContactos() {
        try {
            this.lock.lock();
            return contactos;
        } finally {
            this.lock.unlock();
        }
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public void alteraContactos(Set<Utilizador> contactos) {
        try {
            this.lock.lock();
            if (!this.contactos.containsKey(this.posicao)) {
                this.contactos.put(this.posicao, contactos);
            } else {
                Set<Utilizador> old = this.contactos.remove(this.posicao);
                old.addAll(contactos);
                this.contactos.put(this.posicao,old);
            }
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
        for (Set<Utilizador> s : this.contactos.values()) {
            s.forEach(a->sb.append(a.getUsername()).append(","));
        }
        sb.append("}, infetado=").append(infetado);
        sb.append(", isolamento=").append(isolamento);
        sb.append(", notificado=").append(notificado);
        sb.append("}\n");
        return sb.toString();
    }
}
