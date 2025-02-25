package batalhaNaval;

import java.io.Serializable;

public class Jogada implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String alvo;
	public String linha;
	public String coluna;

	public Jogada(String alvo, String linha, String coluna) {
		this.alvo = alvo;
		this.linha = linha;
		this.coluna = coluna;
	}

	@Override
	public String toString() {
		return "Jogada [alvo=" + alvo + ", linha=" + linha + ", coluna=" + coluna + "]";
	}

}
