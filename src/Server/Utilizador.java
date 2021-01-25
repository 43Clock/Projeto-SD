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
    private boolean aviso;
    private boolean especial;


    public Utilizador(String username, String password, int x, int y) {
        this.username = username;
        this.password = password;
        this.posicao = new Posicao(x, y);
        this.contactos = new HashMap<>();
        this.lock = new ReentrantLock();
        this.infetado = false;
        this.aviso = false;
        this.especial = false;
        if (username.toLowerCase().contains("admin")) {
            this.especial = true;
        }
    }

    /**
     * Getter do Username
     * @return Username
     */
    public String getUsername() {
        try {
            this.lock.lock();
            return username;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Getter da Password
     * @return Password
     */
    public String getPassword() {
        try {
            this.lock.lock();
            return password;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Getter da Posicao
     * @return Posicao
     */
    public Posicao getPosicao() {
        try {
            this.lock.lock();
            return posicao;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Getter do Map de contactos do utilizador
     * @return Map de contactos
     */
    public Map<Posicao, Set<String>> getContactos() {
        try {
            this.lock.lock();
            return contactos;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera a posicão atual de um utilizador
     */
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

    /**
     * Método que indica se user está ou não infetado
     */
    public boolean isInfetado() {
        try {
            this.lock.lock();
            return infetado;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que indica se user é ou não especial
     */
    public boolean isEspecial() {
        try {
            this.lock.lock();
            return especial;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera o estado de infeção e coloca utilizador fora do mapa
     */
    public void infetado() {
        try {
            this.lock.lock();
            this.infetado = true;
            this.posicao = new Posicao(-100, -100); //Isolamento
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que indica se existe uma posicao na lista de contactos
     */
    public boolean temPosicao(int x, int y) {
        try {
            this.lock.lock();
            return this.contactos.keySet().stream().anyMatch(a -> a.getPosX() == x && a.getPosY() == y);
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que faz lock do Utilizador
     */
    public void lock() {
        this.lock.lock();
    }

    /**
     * Método que faz unlock do Utilizador
     */
    public void unlock() {
        this.lock.unlock();
    }

    /**
     * Método para fazer o aviso de infeção
     */
    public void avisa() {
        try {
            this.lock.lock();
            this.aviso = true;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que retira aviso de infeção
     */
    public void tiraAviso() {
        try {
            this.lock.lock();
            this.aviso = false;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Getter do estado do aviso
     * @return
     */
    public boolean temAviso() {
        try {
            this.lock.lock();
            return this.aviso;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que adiciona contactos com outros utilizadores ao historico
     * @param contactos
     */
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
        sb.append(", aviso=").append(aviso);
        sb.append("}\n");
        return sb.toString();
    }
}
