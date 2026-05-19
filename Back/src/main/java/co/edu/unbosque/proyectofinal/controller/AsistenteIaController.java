package co.edu.unbosque.proyectofinal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.HistorialIaDTO;
import co.edu.unbosque.proyectofinal.dto.RespuestaGeminiDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.service.AsistenteIaService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para consumir Gemini desde Angular.
 * Todas las rutas requieren JWT por la configuracion global de seguridad.
 */
@RestController
@RequestMapping({"/api/ia", "/api/ai"})
public class AsistenteIaController {

    private AsistenteIaService asistenteIaService;

    public AsistenteIaController(
            AsistenteIaService asistenteIaService) {

        this.asistenteIaService = asistenteIaService;
    }

    /**
     * Permite conversar con el contacto especial "Asistente IA".
     *
     * @param solicitud datos de la pregunta
     * @param peticionHttp peticion HTTP original
     * @return respuesta generada por Gemini
     */
    @PostMapping("/chat")
    public ResponseEntity<?> consultar(
            @RequestBody SolicitudGeminiDTO solicitud,
            HttpServletRequest peticionHttp) {

        return manejarLlamadoIa(() ->
                asistenteIaService.consultar(
                        solicitud,
                        obtenerIpCliente(peticionHttp),
                        obtenerNavegador(peticionHttp)));
    }

    /**
     * Genera posibles respuestas a un mensaje recibido.
     *
     * @param solicitud mensaje base
     * @param peticionHttp peticion HTTP original
     * @return respuestas sugeridas
     */
    @PostMapping({"/sugerir-respuesta", "/suggest-reply"})
    public ResponseEntity<?> sugerirRespuesta(
            @RequestBody SolicitudGeminiDTO solicitud,
            HttpServletRequest peticionHttp) {

        return manejarLlamadoIa(() ->
                asistenteIaService.sugerirRespuesta(
                        solicitud,
                        obtenerIpCliente(peticionHttp),
                        obtenerNavegador(peticionHttp)));
    }

    /**
     * Resume texto autorizado de una conversacion.
     *
     * @param solicitud contexto visible para el usuario
     * @param peticionHttp peticion HTTP original
     * @return resumen
     */
    @PostMapping({"/resumir", "/summarize"})
    public ResponseEntity<?> resumir(
            @RequestBody SolicitudGeminiDTO solicitud,
            HttpServletRequest peticionHttp) {

        return manejarLlamadoIa(() ->
                asistenteIaService.resumir(
                        solicitud,
                        obtenerIpCliente(peticionHttp),
                        obtenerNavegador(peticionHttp)));
    }

    /**
     * Traduce un mensaje al idioma indicado.
     *
     * @param solicitud mensaje e idioma destino
     * @param peticionHttp peticion HTTP original
     * @return traduccion
     */
    @PostMapping({"/traducir", "/translate"})
    public ResponseEntity<?> traducir(
            @RequestBody SolicitudGeminiDTO solicitud,
            HttpServletRequest peticionHttp) {

        return manejarLlamadoIa(() ->
                asistenteIaService.traducir(
                        solicitud,
                        obtenerIpCliente(peticionHttp),
                        obtenerNavegador(peticionHttp)));
    }

    /**
     * Devuelve el historial reciente de uso del usuario autenticado.
     *
     * @return historial seguro de interacciones con IA
     */
    @GetMapping({"/historial", "/history"})
    public ResponseEntity<List<HistorialIaDTO>> historial() {

        return ResponseEntity.ok(
                asistenteIaService.obtenerHistorial());
    }

    private ResponseEntity<?> manejarLlamadoIa(
            LlamadoIa llamado) {

        try {
            RespuestaGeminiDTO respuesta =
                    llamado.ejecutar();

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(error(e.getMessage()));

        } catch (AccessDeniedException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error(e.getMessage()));

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error(e.getMessage()));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Error inesperado al usar Gemini"));
        }
    }

    private Map<String, String> error(
            String mensaje) {

        return Map.of(
                "error",
                mensaje);
    }

    private String obtenerIpCliente(
            HttpServletRequest peticion) {

        String reenviadoPor =
                peticion.getHeader("X-Forwarded-For");

        if (reenviadoPor != null
                && !reenviadoPor.isBlank()) {

            return reenviadoPor.split(",")[0].trim();
        }

        return peticion.getRemoteAddr();
    }

    private String obtenerNavegador(
            HttpServletRequest peticion) {

        return peticion.getHeader("User-Agent");
    }

    @FunctionalInterface
    private interface LlamadoIa {

        RespuestaGeminiDTO ejecutar();
    }
}
