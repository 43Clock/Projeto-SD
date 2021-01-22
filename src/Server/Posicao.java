package Server;

import java.util.Objects;

public class Posicao {
    private int posX;
    private int posY;

    public Posicao(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posicao posicao = (Posicao) o;
        return posX == posicao.posX && posY == posicao.posY;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("(");
        sb.append(posX);
        sb.append(",").append(posY);
        sb.append(')');
        return sb.toString();
    }
}
