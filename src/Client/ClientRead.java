package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientRead implements Runnable{
    private Socket socket;
    private DataInputStream in;
    private Log log;
    private Syncronizer s;

    public ClientRead(Socket socket, Log log, Syncronizer s) {
        this.socket = socket;
        this.log = log;
        this.s = s;
    }

    /**
     * Método usado para imprimir o mapa de localizações
     * @param msg String que contem o mapa e as suas dimensões
     */
    public void mapa(String msg) {
        String[] args = msg.split("/");
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        List<String> linhas = new ArrayList<>();
        List<String> colunas = new ArrayList<>();
        List<List<String>> dados = new ArrayList<>();
        for (int i = 0; i <= x; i++) {
            linhas.add(Integer.toString(i));
        }
        for (int i = 0; i <= y; i++) {
            colunas.add(Integer.toString(i));
        }
        String[] posicoes = new String[args.length-3];
        System.arraycopy(args, 3, posicoes, 0, args.length - 3);
        for (int i = 0; i <=x*(y+1); i+=y+1) {
            List<String> temp = new ArrayList<>();
            for (int j = i,h = 0; h <=y; j++,h++) {
                temp.add(posicoes[j]);
            }
            dados.add(temp);
        }
        Tabela t = new Tabela(linhas, colunas, dados);
        System.out.println("--- Mapa de posições ---");
        System.out.println(t.toString());
    }

    /**
     * Método run que é executado pela Thread, para interpretar as mensaagens recebidas
     */
    @Override
    public void run() {
        try {
            this.in = new DataInputStream(socket.getInputStream());
            String input;
            boolean sair = true;
            while (sair){ //Logout
                input = this.in.readUTF();
                String[] args = input.split("/");
                switch (args[0]) {
                    case "VALID" -> {
                        log.login();
                    }
                    case "VALIDESPECIAL" -> {
                        log.login();
                        log.especial();
                    }
                    case "NOTVALID" -> {
                        System.out.println("\nUtilizador ou Password errados! ");
                    }
                    case "LOGOUT" -> {
                        log.logout();
                    }
                    case "REGISTERED" -> {
                        System.out.println("\nUtilizador Registado com sucesso!");
                    }
                    case "NOTREGISTERED" -> {
                        System.out.println("\nUtilizador já registado!");
                    }
                    case "MOVED" -> {
                        System.out.println("\nLocalização Atualizada");
                    }
                    case "PESSOAS" -> {
                        System.out.println("\nExistem " + args[1] + " pessoas na localização (" + args[2] + "," + args[3] + ")");
                    }
                    case "VAZIO" -> {
                        System.out.println("\nA posição (" + args[1] + "," + args[2] + ") encontra-se agora vazia.");
                    }
                    case "ALREADY" -> {
                        System.out.println("\nJá se encontra nessa posição");
                    }
                    case "INFETADO" -> {
                        System.out.println("\nInteração com o servidor bloqueada porque se encontra infetado");
                        log.logout();
                    }
                    case "AVISO" -> {
                        System.out.println("\nEsteve em contacto com um Infetado, não saia de casa!");
                    }
                    case "MAPA" -> {
                        mapa(input);
                    }
                    case "SAIR" -> {
                        log.sair();
                        sair = false;
                    }
                    default -> System.out.println("\n");
                }
                if(s.isWaiting())
                    s.stopWaiting();
            }
        } catch (IOException ignored) {

        }
    }
}
