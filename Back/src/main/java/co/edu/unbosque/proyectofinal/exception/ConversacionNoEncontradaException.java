package co.edu.unbosque.proyectofinal.exception;

public class ConversacionNoEncontradaException extends RuntimeException {
    public ConversacionNoEncontradaException() {
        super("Conversacion no encontrada");
    }
}