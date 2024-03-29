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

    /**
     * Método run que é executado pela Thread
     */
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

    /**
     * Método que emite um pedido ao servidor e espera por uma resposta
     * @param s Pedido que vai ser enviado ao servidor
     * @throws IOException
     * @throws InterruptedException
     */
    public void server_send(String s) throws IOException, InterruptedException {
        this.out.writeUTF(s);
        this.out.flush();
        this.s.setWaiting();
    }

    /**
     * Método usado para apresentar os menus
     */
    public void displayMenus() {
        if (this.menu == 1 && log.isEspecial()) {
            this.menu = 2;
        }
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
                System.out.println("+-----------------------------------------+");
                System.out.println("| 1 - Atualizar Posição                   |");
                System.out.println("| 2 - Pessoas numa Localização            |");
                System.out.println("| 3 - Verificar se localização é segura   |");
                System.out.println("| 4 - Anunciar infeção                    |");
                System.out.println("| 5 - Logout                              |");
                System.out.println("+-----------------------------------------+");
                break;
            }
            case 2:{
                System.out.println("+-----------------------------------------+");
                System.out.println("| 1 - Atualizar Posição                   |");
                System.out.println("| 2 - Pessoas numa Localização            |");
                System.out.println("| 3 - Verificar se localização é segura   |");
                System.out.println("| 4 - Anunciar infeção                    |");
                System.out.println("| 5 - Mapa de posições                    |");
                System.out.println("| 6 - Logout                              |");
                System.out.println("+-----------------------------------------+");
                break;
            }
        }
    }

    /**
     * Método para ler a decisão do cliente em cada menu
     * @throws IOException
     * @throws InterruptedException
     */
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
            case 2:{
                menu_three();
                break;
            }
        }
    }

    /**
     * Método que lê um inteiro digitado pelo utilizador.
     * @param max Numero máximo que o input pode ter.
     * @return Inteiro lido
     */
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

    /**
     * Menu inicial que apresenta as opções de fazer login, registar e sair do programa
     * @throws IOException
     * @throws InterruptedException
     */
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

    /**
     * Menu que apresenta as diferentes opções do menu 2, para os users normais
     * @throws IOException
     * @throws InterruptedException
     */
    public void menu_two() throws IOException, InterruptedException {
        int option = lerOpcao(5);
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
                server_send("LOGOUT");
                this.menu = 0;
                break;
            }
        }
    }

    /**
     * Menu que apresenta as diferentes opções do menu 3, para os users com acessos especiais
     * @throws IOException
     * @throws InterruptedException
     */
    public void menu_three() throws IOException, InterruptedException {
        int option = lerOpcao(6);
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
                opcaoMapa();
                break;
            }case 6:{
                server_send("LOGOUT");
                this.menu = 0;
                break;
            }
        }
    }

    /**
     * Método responsável pelo pedido ao servidor do registo de um utilizador
     */
    public void opcaoRegistar() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Nome de Utilizador:");
            String nome = input.readLine();
            System.out.println("Inserir Palavra Passe:");
            String password = input.readLine();
            System.out.println("Inserir Abcissa:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            if(y<0||x<0) throw new NumberFormatException();
            server_send(String.join("/","REGISTAR",nome,password,Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    /**
     * Método responsável pelo pedido ao servidor para fazer o login de um utilizador
     */
    public void opcaoLogin() {
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

    /**
     * Método responsável pelo pedido ao servidor de alterar a posição de um utilizador
     */
    public void opcaoMover() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Abcissa:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            if(y<0||x<0) throw new NumberFormatException();
            server_send(String.join("/","MOVER",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    /**
     * Método responsável pelo pedido ao servidor do numero de pessoas que estão numa certa localização
     */
    public void opcaoPessoasLocalizacao() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Abcissa:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            if(y<0||x<0) throw new NumberFormatException();
            server_send(String.join("/","PESSOAS",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    /**
     * Método responsável pelo pedido ao servidor para saber quando uma certa localização está livre.
     */
    public void opcaoMoverVazio() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Inserir Abcissa:");
            int x = Integer.parseInt(input.readLine());
            System.out.println("Inserir Ordenada:");
            int y = Integer.parseInt(input.readLine());
            if(y<0||x<0) throw new NumberFormatException();
            server_send(String.join("/","VAZIO",Integer.toString(x),Integer.toString(y)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Coordenada Inválida");
        }
    }

    /**
     * Método responsável pela informação ao servidor que o utilizador está infetado
     */
    public void opcaoInfetado() {
        try {
            server_send("INFETADO");
            this.menu = 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método responsável pelo pedido ao servidor do mapa de localização, usado apenas por um utilizador com acessos especiais.
     */
    public void opcaoMapa() {
        try {
            server_send("MAPA");
            this.menu = 2;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


}
