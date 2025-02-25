package batalhaNaval;

import java.util.ArrayList;
import java.util.Random;

public class Jogador {
	private String nome;
	private Tabuleiro tabuleiro;
	private ArrayList<Navio> navios;
	private int totalNavios;
	private String enderecoIP;
	private int portaOrigem;
	public boolean pronto;

	public Jogador(String nome, int linhas, int colunas, int totalNavios, String enderecoIp, int portaorigem) {
		this.nome = nome;
		this.tabuleiro = new Tabuleiro(linhas, colunas);
		this.navios = new ArrayList<>();
		this.totalNavios = totalNavios;
		this.enderecoIP = enderecoIp;
		this.portaOrigem = portaorigem;
		posicionarNavios();
	}
	
	public void resetaTabuleiro(int linhas, int colunas) {
		this.tabuleiro = new Tabuleiro(linhas, colunas);
		this.navios = new ArrayList<>();
		posicionarNavios();
	}

	// adiciona os navios aleatoriamente no tabuleiro
	private void posicionarNavios() {
		Random rand = new Random();
		int linhas = tabuleiro.getLinhas();
		int colunas = tabuleiro.getColunas();

		while (navios.size() < totalNavios) {
			int linha = rand.nextInt(linhas);
			int coluna = rand.nextInt(colunas);

			// garante que o local não tenha outro navio
			if (!temNavioNaPosicao(linha, coluna)) {
				Navio navio = new Navio(linha, coluna);
				navios.add(navio);
				tabuleiro.setPosicao(linha, coluna, 'N'); // adiciona ao no tabuleiro
			}
		}
	}

	// verifica se já tem um navio na posição
	private boolean temNavioNaPosicao(int linha, int coluna) {
		for (Navio navio : navios) {
			if (navio.getLinha() == linha && navio.getColuna() == coluna) {
				return true;
			}
		}
		return false;
	}

	// realiza um ataque em outra posição
	public boolean atacar(Jogador oponente, int linha, int coluna) {
		return oponente.receberAtaque(linha, coluna);
	}

	// processa um ataque recebido
	public boolean receberAtaque(int linha, int coluna) {
		for (Navio navio : navios) {
			if (navio.getLinha() == linha && navio.getColuna() == coluna) {
				navio.destruir();
				tabuleiro.setPosicao(linha, coluna, 'D'); // Marca como destruído
				return true;
			}
		}
		tabuleiro.setPosicao(linha, coluna, 'X'); // Marca como erro
		return false;
	}

	public boolean temNaviosRestantes() {
		for (Navio navio : navios) {
			if (!navio.isDestruido()) {
				return true;
			}
		}
		return false;
	}

	public String exibirTabuleiro() {
		StringBuilder sb = new StringBuilder(); // Usando StringBuilder para construir a string

		sb.append("Tabuleiro de ").append(nome).append("\n"); // Adiciona o nome do tabuleiro

		// Chama o método de exibição do tabuleiro e adiciona a saída ao StringBuilder
		sb.append(tabuleiro.exibirTabuleiro());

		return sb.toString(); // Retorna a string construída
	}

	public String getNome() {
		return nome;
	}

	public String getEnderecoIP() {
		return enderecoIP;
	}

	public void setEnderecoIP(String enderecoIP) {
		this.enderecoIP = enderecoIP;
	}

	public int getPortaOrigem() {
		return portaOrigem;
	}

	public void setPortaOrigem(int portaOrigem) {
		this.portaOrigem = portaOrigem;
	}

}
