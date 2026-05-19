package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDate;

public class UsuarioValidator {

	public static boolean validarUsuario(
			String usuario) {

		if (usuario == null) {
			return false;
		}

		if (usuario.length() < 4) {
			return false;
		}

		if (usuario.length() > 20) {
			return false;
		}

		return true;
	}

	public static boolean validarCorreo(
			String correo) {

		if (correo == null) {
			return false;
		}

		if (!correo.contains("@")) {
			return false;
		}

		if (!correo.contains(".")) {
			return false;
		}

		return true;
	}

	public static boolean validarContrasena(
			String contrasena) {

		if (contrasena == null) {
			return false;
		}

		if (contrasena.length() < 8) {
			return false;
		}

		return true;
	}

	public static boolean validarEdad(
			LocalDate fechaNacimiento) {

		if (fechaNacimiento == null) {
			return false;
		}

		LocalDate hoy =
				LocalDate.now();

		int edad =
				hoy.getYear()
				- fechaNacimiento.getYear();

		if (edad < 13) {
			return false;
		}

		return true;
	}

}