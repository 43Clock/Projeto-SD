package Client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public boolean isLogin() {
        try {
            this.lock.lock();
            return login;
        }finally {
            this.lock.unlock();
        }
    }

    public boolean isEspecial() {
        try {
            this.lock.lock();
            return especial;
        }finally {
            this.lock.unlock();
        }
    }

    public boolean isSair() {
        try {
            this.lock.lock();
            return sair;
        }finally {
            this.lock.unlock();
        }
    }

    public void login() {
        try {
            this.lock.lock();
            this.login = true;
        }finally {
            this.lock.unlock();
        }
    }

    public void especial() {
        try {
            this.lock.lock();
            this.especial = true;
        }finally {
            this.lock.unlock();
        }
    }

    public void logout() {
        try {
            this.lock.lock();
            this.login = false;
            this.especial = false;
        }finally {
            this.lock.unlock();
        }
    }

    public void sair() {
        try {
            this.lock.lock();
            this.sair = true;
        }finally {
            this.lock.unlock();
        }
    }

}
