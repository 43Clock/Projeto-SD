package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class EsperaHandler implements Runnable {
    private DataOutputStream out;
    private Lock lock;
    private Condition condition;
    private Map<String,Utilizador> users;
    private Posicao pos;


    public EsperaHandler(DataOutputStream out, Lock lock, Condition condition, Posicao p, Map<String,Utilizador> mapa) {
        this.out = out;
        this.lock = lock;
        this.condition = condition;
        this.users = mapa;
        this.pos = p;
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

    @Override
    public void run() {
        try {
            this.lock.lock();
            while (!posicaoLivre(pos)) {
                this.condition.await();
            }
            out.writeUTF(String.join("/","VAZIO",Integer.toString(pos.getPosX()),Integer.toString(pos.getPosY())));
            out.flush();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        } finally {
            this.lock.unlock();
        }
    }
}
