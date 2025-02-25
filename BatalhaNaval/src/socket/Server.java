package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import batalhaNaval.BatalhaNaval;
import batalhaNaval.Jogada;
import batalhaNaval.Jogador;
import constants.StatusJogo;
import mensagens.Mensagem;

public class Server {

	public static int totalPlayers = 4;
	public static int quantidadeJogadores = 0;
	public static int quantidadePronto = 0;
	public static int quantidadeRematch = 0;
	public static boolean jogoIniciado = false;

	public static void main(String args[]) throws Exception {
		int portaServidor = 24455;
		DatagramSocket socket = new DatagramSocket(portaServidor);
		BatalhaNaval batalhaNaval = new BatalhaNaval();

		byte[] dadosRecebidos = new byte[6000];

		System.out.println("Servidor Batalha-Naval rodando na porta: " + portaServidor);

		while (true) {
			Arrays.fill(dadosRecebidos, (byte) 0);
			
			try {
				switch (batalhaNaval.status) {
					case AGUARDANDO_JOGADORES:
						processaEsperandoJogadores(socket, dadosRecebidos, batalhaNaval);
						if (quantidadeJogadores == totalPlayers) {
							batalhaNaval.status = StatusJogo.PRONTO_PARA_INICIAR;
							jogoIniciado = true;
							System.out.println("Aguardando jogadores estarem prontos...");
							break;
						}
					case PRONTO_PARA_INICIAR:
						processandoProntoParaIniciar(socket, batalhaNaval);
						break;
	
					case VERIFICA_PRONTO:
						processaVerificaPronto(socket, batalhaNaval, dadosRecebidos);
						if (quantidadePronto == totalPlayers) {
							System.out.println("Jogo Iniciado!");
							batalhaNaval.status = StatusJogo.JOGO_INICIADO;
						}
						break;
	
					case JOGO_INICIADO:
						processaJogoIniciado(socket, dadosRecebidos, batalhaNaval);
						break;
	
					case AGUARDANDO_JOGADA:
						processaEsperandoJogada(socket, batalhaNaval);
						break;
	
					case PROCESSANDO_JOGADA:
						processaProcessandoJogada(socket, dadosRecebidos, batalhaNaval);
						break;
	
					case JOGO_ENCERRADO:
						processaJogoFinalizado(socket, batalhaNaval);
						batalhaNaval.jogadorAtual = 0;
						jogoIniciado = false;
						break;
	
					case AGUARDANDO_REMATCH:
						quantidadeRematch = 0;
						
					    List<Jogador> jogadoresParaRemover = processaAguardandoRematch(socket, dadosRecebidos, batalhaNaval);					    
					    
					    if (quantidadeRematch == totalPlayers) {
					        batalhaNaval.jogadores.removeAll(jogadoresParaRemover);
	
					        batalhaNaval.status = StatusJogo.VALIDA_REMATCH;
					    }
	
					    break;
	
						
					case VALIDA_REMATCH:
						if (quantidadeJogadores == 0) {
							System.out.println("Nenhum jogador aceitou o rematch, encerrando servidor...");
							batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADORES;
							socket.close();
							System.exit(0);
							break;	
							
						} else if (quantidadeJogadores == totalPlayers) {
							String mensagem = "Todos os jogadores optaram por jogar novamente!";
							StatusJogo status = StatusJogo.PRONTO_PARA_INICIAR;
							
							Mensagem mensagemRematch = new Mensagem(0, mensagem);
							dadosRecebidos = Serializador.serializar(mensagemRematch);
							
							for (Jogador jogador : batalhaNaval.jogadores) {
								enviarPacoteParaJogador(socket, dadosRecebidos, jogador);
							}
							jogoIniciado = true;
							batalhaNaval.status = status;
							break;
							
						} else if (quantidadeJogadores < totalPlayers) {
							String mensagem = "Algum jogador optou por não jogar novamente, o servidor será encerrado...";
							StatusJogo status = StatusJogo.AGUARDANDO_JOGADORES;
							
							Mensagem mensagemRematch = new Mensagem(4, mensagem);
							dadosRecebidos = Serializador.serializar(mensagemRematch);
							
							for (Jogador jogador : batalhaNaval.jogadores) {
								enviarPacoteParaJogador(socket, dadosRecebidos, jogador);
							}
							
							batalhaNaval.status = status;
							socket.close();
							System.exit(0);
							break;
							
						}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static List<Jogador> processaAguardandoRematch(DatagramSocket socket, byte[] dadosRecebidos,
			BatalhaNaval batalhaNaval) throws Exception {
		List<Jogador> jogadoresParaRemover = new ArrayList<>();
		while (quantidadeRematch < totalPlayers) {
	        DatagramPacket pacote = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
	        socket.receive(pacote);
	        
	        if (verificaJogadorNovo(pacote, socket)) {
	        	return null;
	        }
	        
	        int portaOrigem = pacote.getPort(); 
	        Mensagem pronto = (Mensagem) Serializador.desserializar(pacote.getData());
	        		
	        for (Jogador jogador : batalhaNaval.jogadores) {
	            if (jogador.getPortaOrigem() == portaOrigem) {
	                if (pronto.getMensagem().equals("s")) {
	                    System.out.println(jogador.getNome() + " está pronto para REMATCH!");
	                    byte[] sendResposta = Serializador
	                            .serializar(new Mensagem(0, "\nAguardando outros jogadores confirmarem..."));
	                    enviarPacoteParaJogador(socket, sendResposta, jogador);
	                    jogador.resetaTabuleiro(10, 10);
	                    
	                } else {
	                    System.out.println(jogador.getNome() + " escolheu não jogar novamente...");
	                    jogadoresParaRemover.add(jogador);
	                    byte[] sendResposta = Serializador
	                            .serializar(new Mensagem(4, "\nOk, obrigado por jogar..."));
	                    enviarPacoteParaJogador(socket, sendResposta, jogador);
	                    quantidadeJogadores--;
	                }
	                
	                quantidadeRematch++;
	                break;
	            }
	        }
	    }
		return jogadoresParaRemover;
		
	}

	private static void processaEsperandoJogadores(DatagramSocket socket, byte[] dadosRecebidos,
			BatalhaNaval batalhaNaval) throws Exception {
		while (quantidadeJogadores < totalPlayers) {
			DatagramPacket pacote = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
			socket.receive(pacote);

			InetAddress ipJogador = pacote.getAddress();
			int portaOrigem = pacote.getPort();
			Mensagem nome = (Mensagem) Serializador.desserializar(pacote.getData());

			System.out.println("Jogador " + nome.getMensagem() + " conectado!");

			Mensagem mensagem = new Mensagem(0, "\nAguardando Jogadores se conectarem...");

			byte[] sendData = Serializador.serializar(mensagem);

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipJogador, portaOrigem);
			socket.send(sendPacket);

			Jogador jogador = new Jogador(nome.getMensagem(), 10, 10, 5, ipJogador.toString(), portaOrigem);
			batalhaNaval.adicionarJogador(jogador);

			quantidadeJogadores++;
		}
	}

	private static void processandoProntoParaIniciar(DatagramSocket socket, BatalhaNaval batalhaNaval)
			throws Exception {
		String mensagem = "Você está pronto? (s/n) ";
		Mensagem pedido = new Mensagem(5, mensagem);
		byte[] sendData = Serializador.serializar(pedido);

		for (Jogador jogador : batalhaNaval.jogadores) {
			enviarPacoteParaJogador(socket, sendData, jogador);

		}
		batalhaNaval.status = StatusJogo.VERIFICA_PRONTO;

	}

	private static void processaVerificaPronto(DatagramSocket socket, BatalhaNaval batalhaNaval, byte[] dadosRecebidos)
			throws Exception {
		DatagramPacket pacote = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
		socket.receive(pacote);
		
		if (verificaJogadorNovo(pacote, socket)) {
			return;
		}

		int portaOrigem = pacote.getPort();
		Mensagem pronto = (Mensagem) Serializador.desserializar(pacote.getData());

		for (Jogador jogador : batalhaNaval.jogadores) {
			if (jogador.getPortaOrigem() == portaOrigem) {
				if (pronto.getMensagem().equals("s")) {
					jogador.pronto = true;
					System.out.println(jogador.getNome() + " está pronto!");
					byte[] sendResposta = Serializador
							.serializar(new Mensagem(0, "\nAguardando outros jogadores estarem pronto..."));
					enviarPacoteParaJogador(socket, sendResposta, jogador);
					quantidadePronto++;
				} else {
					byte[] sendResposta = Serializador
							.serializar(new Mensagem(5, "\nOk, esperando você está pronto...\n\nVocê está pronto?"));
					enviarPacoteParaJogador(socket, sendResposta, jogador);
				}

			}
		}

	}

	private static void processaJogoIniciado(DatagramSocket socket, byte[] dadosRecebidos, BatalhaNaval batalhaNaval)
			throws Exception {
		if (!batalhaNaval.jogoTerminado()) {
			String mensagemCorpo = "\nTurno de " + batalhaNaval.jogadores.get(batalhaNaval.jogadorAtual).getNome()
					+ "\n";

			for (Jogador jogador : batalhaNaval.jogadores) {
				mensagemCorpo += "\n";
				mensagemCorpo += jogador.exibirTabuleiro();
			}

			Mensagem mensagem = new Mensagem(1, mensagemCorpo);

			byte[] sendData = Serializador.serializar(mensagem);

			for (Jogador jogador : batalhaNaval.jogadores) {
				enviarPacoteParaJogador(socket, sendData, jogador);
			}
			
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
		} else {
			batalhaNaval.status = StatusJogo.JOGO_ENCERRADO;
		}

	}

	private static void processaEsperandoJogada(DatagramSocket socket, BatalhaNaval batalhaNaval) throws Exception {
		Jogador jogadorAtual = batalhaNaval.jogadores.get(batalhaNaval.jogadorAtual);
		if (!jogadorAtual.temNaviosRestantes()) {
			byte[] mensagem = Serializador.serializar(new Mensagem(1, "Você perdeu, espere o jogo acabar!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtual);

			batalhaNaval.jogadorAtual = (batalhaNaval.jogadorAtual + 1) % batalhaNaval.jogadores.size();
			batalhaNaval.status = StatusJogo.JOGO_INICIADO;

			return;
		}

		String corpoMensagem = batalhaNaval.escolherAlvo(jogadorAtual);

		Mensagem mensagem = new Mensagem(2, corpoMensagem);

		byte[] sendData = Serializador.serializar(mensagem);

		enviarPacoteParaJogador(socket, sendData, jogadorAtual);

		batalhaNaval.status = StatusJogo.PROCESSANDO_JOGADA;

	}

	private static void processaProcessandoJogada(DatagramSocket socket, byte[] dadosRecebidos,
			BatalhaNaval batalhaNaval) throws Exception {
		Jogador jogadorAtacante = batalhaNaval.jogadores.get(batalhaNaval.jogadorAtual);

		DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
		
		socket.receive(pacoteRecebido);
		
		if (verificaJogadorNovo(pacoteRecebido, socket)) {
			return;
		}

		Jogada ataque = (Jogada) Serializador.desserializar(pacoteRecebido.getData());

		int alvoIndex;
		try {
			alvoIndex = Integer.parseInt(ataque.alvo);
		} catch (NumberFormatException e) {
			byte[] mensagem = Serializador
					.serializar(new Mensagem(0, "\nJogada Inválida, Digite apenas numeros para selecionar o alvo!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
			return;
		}
		
		Jogador alvo;

		if (alvoIndex >= 0 && alvoIndex < batalhaNaval.jogadores.size()) {
			alvo = batalhaNaval.jogadores.get(alvoIndex);
			if (!alvo.temNaviosRestantes()) {
				byte[] mensagem = Serializador
						.serializar(new Mensagem(0, "\nJogador selecionado não possui mais embarcações!\n"));
				enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
				batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
				return;
			}
		} else {
			byte[] mensagem = Serializador.serializar(new Mensagem(0, "\nJogada Inválida, escolha um alvo correto!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
			return;
		}

		if (alvo == jogadorAtacante) {
			byte[] mensagem = Serializador
					.serializar(new Mensagem(0, "\nJogada Inválida, Não pode atacar a si mesmo!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
			return;
		}

		int linha = 0, coluna = 0;

		try {
			linha = Integer.parseInt(ataque.linha);
			coluna = Integer.parseInt(ataque.coluna);
		} catch (NumberFormatException e) {
			byte[] mensagem = Serializador
					.serializar(new Mensagem(0, "\nJogada Inválida, Digite apenas numeros para linha e coluna!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
			return;
		}

		if ((linha < 0 || coluna < 0) || (linha > 9 || coluna > 9)) {
			byte[] mensagem = Serializador.serializar(
					new Mensagem(0, "\nJogada Inválida, Informe coordenadas dentro do limite do tabuleiro!\n"));
			enviarPacoteParaJogador(socket, mensagem, jogadorAtacante);
			batalhaNaval.status = StatusJogo.AGUARDANDO_JOGADA;
			return;
		}

		boolean resultado = jogadorAtacante.atacar(alvo, linha, coluna);

		String resultadoAtaque;
		if (resultado) {
			resultadoAtaque = "\nO jogador " + alvo.getNome() + " teve um navio destruído por "
					+ jogadorAtacante.getNome() + "\n";
		} else {
			resultadoAtaque = "\nO jogador " + jogadorAtacante.getNome() + " errou o tiro em " + alvo.getNome() + "\n";
		}

		Mensagem mensagemResultado = new Mensagem(3, resultadoAtaque);

		byte[] sendDataResultado = Serializador.serializar(mensagemResultado);

		for (Jogador jogador : batalhaNaval.jogadores) {
			enviarPacoteParaJogador(socket, sendDataResultado, jogador);
		}

		batalhaNaval.jogadorAtual = (batalhaNaval.jogadorAtual + 1) % batalhaNaval.jogadores.size();

		batalhaNaval.status = StatusJogo.JOGO_INICIADO;

	}

	private static void processaJogoFinalizado(DatagramSocket socket, BatalhaNaval batalhaNaval) throws Exception {
		Jogador vencedor = batalhaNaval.obterVencedor();
		Mensagem resultado = new Mensagem(5,
				"\nJogo Encerrado!\n" + vencedor.getNome() + " é o vencedor!\nDeseja jogar novamente? (s/n)");

		byte[] sendData = Serializador.serializar(resultado);

		for (Jogador jogador : batalhaNaval.jogadores) {
			String enderecoIP = jogador.getEnderecoIP().replace("/", "");
			InetAddress ipJogador = InetAddress.getByName(enderecoIP);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipJogador,
					jogador.getPortaOrigem());
			socket.send(sendPacket);
			jogador.pronto = false;
		}
		
		System.out.println("\nJogo terminado!");
		System.out.println("\n" + vencedor.getNome() + " é o vencedor!");

		quantidadePronto = 0;

		batalhaNaval.status = StatusJogo.AGUARDANDO_REMATCH;
	}
	
	public static boolean verificaJogadorNovo(DatagramPacket pacote, DatagramSocket socket) throws ClassNotFoundException, IOException {
		Object pacoteRecebido = Serializador.desserializar(pacote.getData());
		
		if (pacoteRecebido instanceof Mensagem) {
			Mensagem verificaStatus = (Mensagem) pacoteRecebido;

			if (verificaStatus.getStatus() == 0 && jogoIniciado) {
				Mensagem mensagem = new Mensagem(4, "\nJogo já iniciado, tente novamente mais tarde...");
				InetAddress ipJogador = pacote.getAddress();
				int portaOrigem = pacote.getPort();
				byte[] sendData = Serializador.serializar(mensagem);

				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipJogador, portaOrigem);
				socket.send(sendPacket);
				return true;
			}
		}
		return false;
	}

	private static void enviarPacoteParaJogador(DatagramSocket socket, byte[] sendData, Jogador jogador)
			throws Exception {
		String enderecoIP = jogador.getEnderecoIP().replace("/", "");
		InetAddress ipJogador = InetAddress.getByName(enderecoIP);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipJogador, jogador.getPortaOrigem());
		socket.send(sendPacket);
	}
}
