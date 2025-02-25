package socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializador {

    // Método para serializar objetos
    public static byte[] serializar(Object objeto) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(objeto);
            objectOutputStream.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }

    // Método para desserializar objetos
    public static Object desserializar(byte[] dadosRecebidos) throws IOException, ClassNotFoundException {
        if (dadosRecebidos == null || dadosRecebidos.length == 0) {
            throw new IOException("Dados recebidos vazios ou nulos");
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dadosRecebidos);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        } catch (IOException e) {
            // Trata EOFException explicitamente, ou qualquer erro de I/O
            System.err.println("Erro de leitura no fluxo de dados: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
            // Caso o tipo de objeto não seja encontrado
            System.err.println("Classe não encontrada durante a desserialização: " + e.getMessage());
            throw e;
        }
    }
}


