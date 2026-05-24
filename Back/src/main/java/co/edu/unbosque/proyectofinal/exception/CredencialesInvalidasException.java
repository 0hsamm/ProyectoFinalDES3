package co.edu.unbosque.proyectofinal.exception;

public class CredencialesInvalidasException extends RuntimeException {

	public CredencialesInvalidasException() {

		super(
			"Credenciales incorrectas");
	}

}