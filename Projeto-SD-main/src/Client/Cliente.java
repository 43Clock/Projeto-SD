package Client;

import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 12345);

        Log l = new Log();
        Syncronizer sync = new Syncronizer();
        Thread reader = new Thread(new ClientRead(s,l,sync));
        Thread writer = new Thread(new ClienteWrite(s,l,sync));
        reader.start();
        writer.start();
    }
}
