package co.edu.unbosque.proyectofinal.exception;

public class MensajeNoEncontradoException extends RuntimeException {
    public MensajeNoEncontradoException() {
        super("Mensaje no encontrado");
    }
}