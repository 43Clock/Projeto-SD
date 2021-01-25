package Client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Syncronizer {
    private boolean waiting;
    private Lock lock;
    private Condition cond;

    public Syncronizer() {
        this.waiting = false;
        this.lock = new ReentrantLock();
        this.cond = this.lock.newCondition();
    }

    public boolean isWaiting() {
        try {
            this.lock.lock();
            return this.waiting;
        }finally {
            this.lock.unlock();
        }
    }

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
