package Client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que permite saber se um utilizador está ou não logged in ou não, se é um utilizador especial ou se pertende sair do programa
 */
public class Log {
    private boolean login;
    private boolean especial;
    private boolean sair;
    private Lock lock;

    public Log() {
        this.login = false;
        this.sair = false;
        this.lock = new ReentrantLock();
    }

    /**
     * Getter do estado login
     * @return
     */
    public boolean isLogin() {
        try {
            this.lock.lock();
            return login;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Getter do nivel de especialidade do user
     * @return
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
     * Getter do estado sair
     * @return
     */
    public boolean isSair() {
        try {
            this.lock.lock();
            return sair;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera o estado para login
     */
    public void login() {
        try {
            this.lock.lock();
            this.login = true;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera o estado para especial
     */
    public void especial() {
        try {
            this.lock.lock();
            this.especial = true;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera o estado para logout
     */
    public void logout() {
        try {
            this.lock.lock();
            this.login = false;
            this.especial = false;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que altera o estado para sair
     */
    public void sair() {
        try {
            this.lock.lock();
            this.sair = true;
        }finally {
            this.lock.unlock();
        }
    }

}
