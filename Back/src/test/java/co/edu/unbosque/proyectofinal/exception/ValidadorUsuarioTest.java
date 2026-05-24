package co.edu.unbosque.proyectofinal.exception;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ValidadorUsuarioTest {

    @Test
    void verificarUsuarioAceptaUsuarioValido() {
        assertDoesNotThrow(() ->
                ValidadorUsuario.verificarUsuario("usuario1"));
    }

    @Test
    void verificarUsuarioRechazaUsuarioCorto() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidadorUsuario.verificarUsuario("abc"));
    }

    @Test
    void verificarCorreoAceptaCorreoValido() {
        assertDoesNotThrow(() ->
                ValidadorUsuario.verificarCorreo("usuario@correo.com"));
    }

    @Test
    void verificarCorreoRechazaCorreoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidadorUsuario.verificarCorreo("correo-invalido"));
    }

    @Test
    void verificarContrasenaRechazaContrasenaCorta() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidadorUsuario.verificarContrasena("123"));
    }

    @Test
    void verificarEdadRechazaMenorDeEdadPermitida() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ValidadorUsuario.verificarEdad(
                        LocalDate.now().minusYears(10)));
    }
}
