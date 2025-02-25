package batalhaNaval;

public class Tabuleiro {
	private char[][] grid;
	private int linhas;
	private int colunas;

	public Tabuleiro(int linhas, int colunas) {
		this.linhas = linhas;
		this.colunas = colunas;
		this.grid = new char[linhas][colunas];

		// inicia o tabuleiro com os espaços vazios
		for (int i = 0; i < linhas; i++) {
			for (int j = 0; j < colunas; j++) {
				grid[i][j] = ' ';
			}
		}
	}

	// define um símbolo do jogo em uma posição
	public void setPosicao(int linha, int coluna, char simbolo) {
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			grid[linha][coluna] = simbolo;
		}
	}

	// pega o símbolo de uma posição específica
	public char getPosicao(int linha, int coluna) {
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			return grid[linha][coluna];
		}
		return ' '; // retorna vazio caso seja uma posição invalida
	}

	// mostrar o tabuleiro no console
	public String exibirTabuleiro() {
		StringBuilder sb = new StringBuilder();

		sb.append("   ");
		for (int i = 0; i < colunas; i++) {
			sb.append(i).append(" ");
		}
		sb.append("\n");

		sb.append("  ┌");
		for (int i = 0; i < colunas - 1; i++) {
			sb.append("─┬");
		}
		sb.append("─┐\n");

		for (int i = 0; i < linhas; i++) {
			sb.append(i).append(" │");
			for (int j = 0; j < colunas; j++) {
				sb.append(grid[i][j]).append("│");
			}
			sb.append("\n");

			if (i < linhas - 1) {
				sb.append("  ├");
				for (int j = 0; j < colunas - 1; j++) {
					sb.append("─┼");
				}
				sb.append("─┤\n");
			}
		}

		sb.append("  └");
		for (int i = 0; i < colunas - 1; i++) {
			sb.append("─┴");
		}
		sb.append("─┘\n");

		return sb.toString();
	}

	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}
}
