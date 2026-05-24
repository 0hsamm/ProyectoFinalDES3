package co.edu.unbosque.proyectofinal.exception;

public class FraseSecretaInvalidaException extends RuntimeException {
    public FraseSecretaInvalidaException() {
        super("Frase secreta invalida o no configurada");
    }
}