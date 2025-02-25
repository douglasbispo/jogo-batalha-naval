package batalhaNaval;

import java.util.ArrayList;
import java.util.Scanner;

import constants.StatusJogo;

public class BatalhaNaval {
	public ArrayList<Jogador> jogadores;
	public int jogadorAtual;
	private Scanner scanner;
	public StatusJogo status = StatusJogo.AGUARDANDO_JOGADORES;

	public BatalhaNaval() {
		this.jogadores = new ArrayList<>();
		this.jogadorAtual = 0;
		this.scanner = new Scanner(System.in);

	}

	public void adicionarJogador(Jogador novoJogador) {
		this.jogadores.add(novoJogador);
	}

	public boolean jogoTerminado() {
		int jogadoresComNavios = 0;
		for (Jogador jogador : jogadores) {
			if (jogador.temNaviosRestantes()) {
				jogadoresComNavios++;
			}
		}
		return jogadoresComNavios <= 1;
	}

	public Jogador obterVencedor() {
		for (Jogador jogador : jogadores) {
			if (jogador.temNaviosRestantes()) {
				return jogador;
			}
		}
		return null;
	}

	public String escolherAlvo(Jogador atacante) {
		StringBuilder listaJogadores = new StringBuilder();
		listaJogadores.append("Jogadores disponíveis para atacar: \n");

		for (int i = 0; i < jogadores.size(); i++) {
			if (!jogadores.get(i).equals(atacante) && jogadores.get(i).temNaviosRestantes()) {
				listaJogadores.append(i).append(" - ").append(jogadores.get(i).getNome()).append("\n");
			}
		}

		return listaJogadores.toString();
	}

	public Jogador escolherAlvoOld(Jogador atacante) {
		System.out.println("Escolha um jogador para atacar: ");
		for (int i = 0; i < jogadores.size(); i++) {
			if (!jogadores.get(i).equals(atacante)) {
				System.out.println(i + " - " + jogadores.get(i).getNome());
			}
		}
		int escolha;
		do {
			System.out.print("Número do jogador: ");
			escolha = scanner.nextInt();
		} while (escolha < 0 || escolha >= jogadores.size() || jogadores.get(escolha).equals(atacante));

		return jogadores.get(escolha);
	}
}
