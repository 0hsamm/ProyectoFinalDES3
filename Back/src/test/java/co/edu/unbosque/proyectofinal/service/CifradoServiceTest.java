package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CifradoServiceTest {

    private CifradoService cifradoService =
            new CifradoService();

    @Test
    void generaHashSinGuardarLaFraseEnTextoPlano() {
        String hash =
                cifradoService.generarHashFrase(
                        "frase segura 123");

        assertTrue(hash.startsWith("PBKDF2$"));
        assertFalse(hash.contains("frase segura 123"));
        assertTrue(
                cifradoService.validarFrase(
                        "frase segura 123",
                        hash));
    }

    @Test
    void cifraYDescifraMensajeConFraseCorrecta() {
        String hash =
                cifradoService.generarHashFrase(
                        "frase segura 123");

        CifradoService.ResultadoCifrado resultado =
                cifradoService.cifrar(
                        "Mensaje academico privado",
                        "frase segura 123",
                        hash);

        assertNotEquals(
                "Mensaje academico privado",
                resultado.getTextoCifrado());

        String descifrado =
                cifradoService.descifrar(
                        resultado.getTextoCifrado(),
                        resultado.getIv(),
                        "frase segura 123",
                        hash);

        assertEquals(
                "Mensaje academico privado",
                descifrado);
    }

    @Test
    void rechazaDescifradoConFraseIncorrecta() {
        String hash =
                cifradoService.generarHashFrase(
                        "frase segura 123");

        CifradoService.ResultadoCifrado resultado =
                cifradoService.cifrar(
                        "Mensaje academico privado",
                        "frase segura 123",
                        hash);

        assertThrows(
                IllegalArgumentException.class,
                () -> cifradoService.descifrar(
                        resultado.getTextoCifrado(),
                        resultado.getIv(),
                        "otra frase 456",
                        hash));
    }
}
