package Client;

import java.util.List;

public class Tabela {
    private final List<String> linhaDesc;
    private final List<String> colunasDesc;
    private final List<List<String>> lista;
    private final StringBuilder string;

    public Tabela(List<String> linhaDesc, List<String> colunasDesc, List<List<String>> lista) {
        this.linhaDesc = linhaDesc;
        this.colunasDesc = colunasDesc;
        this.lista = lista;
        this.string = new StringBuilder();
    }

    private String repeat(String s,int n){
        return String.valueOf(s).repeat(Math.max(0, n));
    }

    private void printSeparador(int[] sizeCols){
        for(int i = 0;i<=sizeCols.length-1;i++)
            this.string.append("+").append(repeat("-",sizeCols[i]));
        this.string.append("+\n");
    }

    public String toString(){
        string.setLength(0);
        int numCol = this.colunasDesc.size();
        int numLin = this.linhaDesc.size();

        int[] sizeCols = new int[numCol+1];
        int size = 0;
        for(String s:this.linhaDesc) size = Math.max(size, s.length());
        sizeCols[0] = size+2;
        for(int j = 0;j<numCol;j++){
            sizeCols[j+1] = this.colunasDesc.get(j).length()+2;
            for(int i = 0;i<numLin;i++)
                sizeCols[j+1] = Math.max(sizeCols[j + 1], this.lista.get(i).get(j).length() + 2);
        }
        this.printSeparador(sizeCols);
        string.append("|");
        string.append(repeat(" ",sizeCols[0]));
        for(int j = 0;j<numCol;j++){
            string.append("| ").append(this.colunasDesc.get(j));
            string.append(repeat(" ",sizeCols[j+1]-this.colunasDesc.get(j).length()-1));
        }
        string.append("|\n");
        this.printSeparador(sizeCols);

        for(int i = 0;i<numLin;i++){
            string.append("| ").append(this.linhaDesc.get(i));
            string.append(repeat(" ",sizeCols[0]-this.linhaDesc.get(i).length()-1));
            for(int j = 0;j<numCol;j++){
                string.append("| ").append(this.lista.get(i).get(j));
                string.append(repeat(" ",sizeCols[j+1] -this.lista.get(i).get(j).length()-1));
            }
            string.append("|\n");
            this.printSeparador(sizeCols);
        }
        return string.toString();
    }
}
