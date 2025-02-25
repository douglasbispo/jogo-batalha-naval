package batalhaNaval;

public class Navio {
    private int linha;
    private int coluna;
    private boolean destruido;

    public Navio(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.destruido = false;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public boolean isDestruido() {
        return destruido;
    }

    public void destruir() {
        this.destruido = true;
    }
}

