package co.edu.unbosque.proyectofinal.exception;

import java.time.LocalDate;
import java.time.Period;

/**
 * Clase utilitaria encargada de validar reglas basicas
 * relacionadas con el registro de usuarios.
 *
 * <p>No es un servicio de Spring porque no maneja estado,
 * no consulta repositorios y no requiere inyeccion de dependencias.</p>
 */
public final class ValidadorUsuario {

    private static final int LONGITUD_MINIMA_USUARIO = 4;

    private static final int LONGITUD_MAXIMA_USUARIO = 20;

    private static final int LONGITUD_MINIMA_CONTRASENA = 8;

    private static final int EDAD_MINIMA = 13;

    private ValidadorUsuario() {
    }

    /**
     * Valida que el nombre de usuario tenga una longitud permitida.
     *
     * @param usuario nombre de usuario a validar
     */
    public static void verificarUsuario(
            String usuario) {

        if (usuario == null
                || usuario.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El usuario no puede estar vacio");
        }

        String usuarioLimpio =
                usuario.trim();

        if (usuarioLimpio.length()
                < LONGITUD_MINIMA_USUARIO) {

            throw new IllegalArgumentException(
                    "El usuario debe tener minimo 4 caracteres");
        }

        if (usuarioLimpio.length()
                > LONGITUD_MAXIMA_USUARIO) {

            throw new IllegalArgumentException(
                    "El usuario debe tener maximo 20 caracteres");
        }
    }

    /**
     * Valida formato basico de correo electronico.
     *
     * @param correo correo a validar
     */
    public static void verificarCorreo(
            String correo) {

        if (correo == null
                || correo.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El correo no puede estar vacio");
        }

        String correoLimpio =
                correo.trim();

        if (!correoLimpio.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Correo invalido");
        }
    }

    /**
     * Valida que la contrasena cumpla la longitud minima.
     *
     * @param contrasena contrasena a validar
     */
    public static void verificarContrasena(
            String contrasena) {

        if (contrasena == null
                || contrasena.length()
                < LONGITUD_MINIMA_CONTRASENA) {

            throw new IllegalArgumentException(
                    "La contrasena debe tener minimo 8 caracteres");
        }
    }

    /**
     * Valida que el usuario tenga la edad minima permitida.
     *
     * @param fechaNacimiento fecha de nacimiento del usuario
     */
    public static void verificarEdad(
            LocalDate fechaNacimiento) {

        if (fechaNacimiento == null) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento es obligatoria");
        }

        if (fechaNacimiento.isAfter(
                LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha de nacimiento no puede ser futura");
        }

        int edad =
                Period.between(
                        fechaNacimiento,
                        LocalDate.now())
                        .getYears();

        if (edad < EDAD_MINIMA) {
            throw new IllegalArgumentException(
                    "Debes tener minimo 13 anos");
        }
    }
}
