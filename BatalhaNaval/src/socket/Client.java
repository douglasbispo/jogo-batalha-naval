package socket;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import batalhaNaval.Jogada;
import mensagens.Mensagem;

public class Client {
	public static void main(String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
		InetAddress enderecoServidor = InetAddress.getByName("localhost");
		int portaServidor = 24455;
		byte[] dadosRecebidos = new byte[6000];

		System.out.print("Deseja jogar batalha naval? (s/n) ");
		String resposta = kb.readLine();

		if (resposta.toLowerCase().equals("s")) {
			System.out.print("Informe seu nome: ");
			String nome = kb.readLine();
			
			Mensagem mensagem = new Mensagem(0, nome);
			byte[] mensagemBytes = Serializador.serializar(mensagem);

			DatagramPacket pacote = new DatagramPacket(mensagemBytes, mensagemBytes.length, enderecoServidor,
					portaServidor);

			socket.send(pacote);
		} else {
			System.out.println("Sem problema, jogaremos na próxima...");
			socket.close();
			System.exit(0);
		}

		while (true) {
			Arrays.fill(dadosRecebidos, (byte) 0);
			
			DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);

			socket.receive(pacoteRecebido);

			int bytesRecebidos = pacoteRecebido.getLength();

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pacoteRecebido.getData(), 0,
					bytesRecebidos);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			Mensagem mensagemRecebida = (Mensagem) objectInputStream.readObject();

			if (pacoteRecebido != null) {
				switch (mensagemRecebida.getStatus()) {
				case 0:
					System.out.println(mensagemRecebida.getMensagem());
					break;

				case 1:
					System.out.println(mensagemRecebida.getMensagem());
					break;
					
				case 2:
					System.out.println("\n" + mensagemRecebida.getMensagem());
					System.out.println("Faça sua jogada!");

					System.out.print("Informe o jogador que deseja atacar: ");
					String alvo = kb.readLine();
					System.out.print("Infome a linha que deseja realizar o ataque: ");
					String linha = kb.readLine();
					System.out.print("Informe a coluna que deseja realizar o ataque: ");
					String coluna = kb.readLine();

					Jogada ataque = new Jogada(alvo, linha, coluna);

					byte[] sendData = Serializador.serializar(ataque);

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, enderecoServidor,
							portaServidor);
					
					socket.send(sendPacket);

					break;
				case 3:
					System.out.println(mensagemRecebida.getMensagem());
					break;
				case 4:
					System.out.println(mensagemRecebida.getMensagem());
					socket.close();
					System.exit(0);
					break;
				case 5:
					System.out.print(mensagemRecebida.getMensagem());
					String pronto = kb.readLine();
					
					Mensagem respostaConfirma = new Mensagem(1, pronto);
					byte[] mensagemBytes = Serializador.serializar(respostaConfirma);

					DatagramPacket pacote = new DatagramPacket(mensagemBytes, mensagemBytes.length, enderecoServidor,
							portaServidor);

					socket.send(pacote);
					
				}
			}
		}
	}
}
