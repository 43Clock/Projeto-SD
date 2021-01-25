package Client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que permite fazer a sincronização entre as Threads ClienteWrite e ClientRead, para que quando o utilizador manda
 * um request ao servidor, para poder mandar outro request tem de receber uma resposta primeiro
 */
public class Syncronizer {
    private boolean waiting;
    private Lock lock;
    private Condition cond;

    public Syncronizer() {
        this.waiting = false;
        this.lock = new ReentrantLock();
        this.cond = this.lock.newCondition();
    }

    /**
     * Método para verificar se Thread ClienteWrite esta em espera
     * @return
     */
    public boolean isWaiting() {
        try {
            this.lock.lock();
            return this.waiting;
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método que coloca a Thread ClienteWrite em espera
     * @throws InterruptedException
     */
    public void setWaiting() throws InterruptedException {
        try {
            this.lock.lock();
            this.waiting = true;
            while (this.waiting)
                this.cond.await();
        }finally {
            this.lock.unlock();
        }
    }

    /**
     * Método usado pela Thread ClientRead que sinaliza a Thread ClienteWrite para sair do estado de espera
     */
    public void stopWaiting() {
        try {
            this.lock.lock();
            this.waiting = false;
            this.cond.signalAll();
        }finally {
            this.lock.unlock();
        }
    }
}
