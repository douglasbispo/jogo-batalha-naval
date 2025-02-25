package mensagens;

import java.io.Serializable;

import batalhaNaval.Jogada;

public class Mensagem implements Serializable {
	private static final long serialVersionUID = 1L;

	private int status;
	private String mensagem;

	public Mensagem(int status, String mensagem) {
		this.status = status;
		this.mensagem = mensagem;
	}

	public int getStatus() {
		return status;
	}

	public String getMensagem() {
		return mensagem;
	}

	@Override
	public String toString() {
		return "Mensagem [status=" + status + ", mensagem=" + mensagem + "]";
	}
	
	

}
