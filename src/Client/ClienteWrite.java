package Client;

import java.io.*;
import java.net.Socket;

public class ClienteWrite implements Runnable {
    private int menu;
    private Socket socket;
    private DataOutputStream out;
    private Log log;
    private Syncronizer s;


    public ClienteWrite(Socket socket, Log log, Syncronizer s) {
        this.menu = 0;
        this.socket = socket;
        this.log = log;
        this.s = s;
    }

    public void run() {
        try {
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            while (!log.isSair()) {
                displayMenus();
                menu_option();
            }
            socket.close();

        } catch (IOException | InterruptedException e) {
            System.out.println("Closing...");
        }
    }

    public void server_send(String s) throws IOException, InterruptedException {
        this.out.writeUTF(s);
        this.out.flush();
        this.s.setWaiting();
    }

    public void displayMenus() {
        switch (this.menu) {
            case 0:{
                System.out.println("\n+------------------+");
                System.out.println("| 1 - Fazer Login  |");
                System.out.println("| 2 - Registar     |");
                System.out.println("| 3 - Sair         |");
                System.out.println("+------------------+");
                break;
            }
            case 1:{
                System.out.println("+-----------------");
                System.out.println("| 1 - Atualizar Posição");
                System.out.println("| 2 - Pessoas numa Localização");
                System.out.println("| 3 - Mover quando estiver vazio");
                System.out.println("| 4 - Anunciar infeção");
                System.out.println("| 5 - ");
                System.out.println("| 6 - Logout");
                System.out.println("+-----------------");
                break;
            }
        }
    }

    public void menu_option() throws IOException, InterruptedException {
        switch (this.menu) {
            case 0:{
                menu_one();
                break;
            }
            case 1:{
                menu_two();
                break;
            }
        }
    }

    public int lerOpcao(int max) {
        System.out.print("Opção: ");
        int option = -1;
        String s;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (option == -1) {
            try {
                s = input.readLine();
                option = Integer.parseInt(s);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Opção Inválida");
            }
            if (option > max || option<1) {
                System.out.println("Opção Inválida");
                option = -1;
            }
        }
        return option;
    }

    public void menu_one() throws IOException, InterruptedException {
        int option = lerOpcao(3);

        switch (option) {
            case 1:{
                opcaoLogin();
                break;
            }
            case 2:{
                opcaoRegistar();
                break;
            }
            case 3:{
                server_send("SAIR");
                break;
            }
        }
        if(this.log.isLogin()) this.menu = 1;
    }

    public void menu_two() throws IOException, InterruptedException {
        int option = lerOpcao(7);
        switch (option) {
            case 1:{
                opcaoMover();
                break;
            }case 2:{
                opcaoPessoasLocalizacao();
                break;
            }case 3:{
                opcaoMoverVazio();
                break;
            }case 4:{
                opcaoInfetado();
                break;
            }case 5:{
                break;
            }case 6:{
                server_send("LOGOUT");
                this.menu = 0;
                break;
            }
        }
    }

    public void opcaoRegistar() { //@TODO UTILIZADOR JA EXISTE E COORDENADAS MAL
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Nome de Utilizador:");
            String nome = input.readLine();
            System.out.println("Inserir Palavra Passe:");
            String password = input.readLine();
            System.out.println("Inserir Coordenada:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            server_send(String.join("/","REGISTAR",nome,password,Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    public void opcaoLogin() { //@TODO CASO DE PASSWORD ERRADA
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Nome de Utilizador:");
            String nome = input.readLine();
            System.out.println("Inserir Palavra Passe:");
            String password = input.readLine();
            server_send(String.join("/","LOGIN",nome,password));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void opcaoMover() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Coordenada:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            server_send(String.join("/","MOVER",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    public void opcaoPessoasLocalizacao() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Coordenada:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            server_send(String.join("/","PESSOAS",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    public void opcaoMoverVazio() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Coordenada:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            server_send(String.join("/","VAZIO",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    public void opcaoInfetado() {
        try {
            server_send("INFETADO");
            this.menu = 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
