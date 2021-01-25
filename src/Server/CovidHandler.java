package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Classe que é responsável por notificar um utilizador quando esteve em contacto com um infetado.
 */
public class CovidHandler implements Runnable {
    private DataOutputStream out;
    private Lock lock;
    private Condition condition;
    private Utilizador utilizador;

    public CovidHandler(DataOutputStream out, Lock lock, Condition condition, Utilizador utilizador) {
        this.out = out;
        this.lock = lock;
        this.condition = condition;
        this.utilizador = utilizador;
    }

    /**
     * Método run que é executado pela Thread.
     */
    @Override
    public void run() {
        while (true) {
            try {
                this.lock.lock();
                while (!utilizador.temAviso()) {
                    this.condition.await();
                }
                this.utilizador.tiraAviso();
                out.writeUTF("AVISO");
                out.flush();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                this.lock.unlock();
            }
        }
    }
}
