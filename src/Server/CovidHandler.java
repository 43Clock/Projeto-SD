package Server;

import javax.xml.crypto.Data;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(this.utilizador.temAviso());
                this.lock.lock();
                while (!utilizador.temAviso()) {
                    this.condition.await();
                }
                this.utilizador.tiraAviso();
                out.writeUTF("AVISO");
                out.flush();
            } catch (InterruptedException | IOException ignored) {

            } finally {
                this.lock.unlock();
            }
        }
    }
}
