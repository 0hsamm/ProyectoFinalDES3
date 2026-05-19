package co.edu.unbosque.proyectofinal.exception;

public class CorreoYaExisteException extends RuntimeException {

	public CorreoYaExisteException() {

		super(
			"El correo ya está registrado");
	}

}