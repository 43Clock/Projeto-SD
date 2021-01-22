package Client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Log {
    private boolean login;
    private boolean logout;
    private boolean sair;
    private Lock lock;

    public Log() {
        this.login = false;
        this.logout = false;
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

    public boolean isLogout() {
        try {
            this.lock.lock();
            return logout;
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
            this.logout = false;
            this.login = true;
        }finally {
            this.lock.unlock();
        }
    }

    public void logout() {
        try {
            this.lock.lock();
            this.logout = true;
            this.login = false;
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
