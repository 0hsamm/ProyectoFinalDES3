package co.edu.unbosque.proyectofinal.exception;

public class UsuarioNoHabilitadoException extends RuntimeException {
    public UsuarioNoHabilitadoException() {
        super("El usuario no ha verificado su cuenta");
    }
}