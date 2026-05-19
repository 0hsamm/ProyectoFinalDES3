package co.edu.unbosque.proyectofinal.exception;

public class UsuarioYaExisteException extends RuntimeException {

	public UsuarioYaExisteException() {

		super(
			"El usuario ya existe");
	}

}