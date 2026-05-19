package co.edu.unbosque.proyectofinal.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.unbosque.proyectofinal.dto.HistorialIaDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.dto.RespuestaGeminiDTO;
import co.edu.unbosque.proyectofinal.entity.InteraccionIa;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import co.edu.unbosque.proyectofinal.repository.InteraccionIaRepository;

/**
 * Servicio responsable de comunicar el backend con Gemini.
 * La API key se lee desde variables de entorno y nunca se expone a Angular.
 */
@Service
public class AsistenteIaService {

    private static final int MAXIMO_CARACTERES_MENSAJE = 4000;

    private static final int MAXIMO_CARACTERES_CONTEXTO = 8000;

    private final SeguridadIaService seguridadIaService;

    private final AuditoriaService auditoriaService;

    private final InteraccionIaRepository interaccionIaRepository;

    private final HttpClient httpClient;

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com}")
    private String baseUrl;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    public AsistenteIaService(
            SeguridadIaService seguridadIaService,
            AuditoriaService auditoriaService,
            InteraccionIaRepository interaccionIaRepository) {

        this.seguridadIaService = seguridadIaService;
        this.auditoriaService = auditoriaService;
        this.interaccionIaRepository = interaccionIaRepository;
        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(15))
                        .build();
    }

    /**
     * Envia una pregunta libre al Asistente IA.
     *
     * @param solicitud solicitud del usuario
     * @param ip direccion IP de la peticion
     * @param navegador navegador o cliente usado
     * @return respuesta generada por Gemini
     */
    public RespuestaGeminiDTO consultar(
            SolicitudGeminiDTO solicitud,
            String ip,
            String navegador) {

        validarTexto(
                solicitud.getMensaje(),
                "La pregunta no puede estar vacia",
                MAXIMO_CARACTERES_MENSAJE);

        String instruccion =
                construirInstruccionConsulta(solicitud);

        return ejecutarAccionGemini(
                solicitud,
                TipoAccionIa.CONSULTA,
                instruccion,
                ip,
                navegador);
    }

    /**
     * Genera respuestas sugeridas para un mensaje recibido.
     *
     * @param solicitud mensaje o contexto autorizado
     * @param ip direccion IP de la peticion
     * @param navegador navegador o cliente usado
     * @return respuesta sugerida
     */
    public RespuestaGeminiDTO sugerirRespuesta(
            SolicitudGeminiDTO solicitud,
            String ip,
            String navegador) {

        validarTexto(
                solicitud.getMensaje(),
                "El mensaje base no puede estar vacio",
                MAXIMO_CARACTERES_MENSAJE);

        String instruccion =
                construirInstruccionSugerencia(solicitud);

        return ejecutarAccionGemini(
                solicitud,
                TipoAccionIa.SUGERIR_RESPUESTA,
                instruccion,
                ip,
                navegador);
    }

    /**
     * Resume texto de una conversacion que el usuario ya puede ver.
     *
     * @param solicitud contexto autorizado por el usuario
     * @param ip direccion IP de la peticion
     * @param navegador navegador o cliente usado
     * @return resumen generado por Gemini
     */
    public RespuestaGeminiDTO resumir(
            SolicitudGeminiDTO solicitud,
            String ip,
            String navegador) {

        validarTexto(
                solicitud.getContexto(),
                "Debes enviar el texto autorizado que quieres resumir",
                MAXIMO_CARACTERES_CONTEXTO);

        String instruccion =
                construirInstruccionResumen(solicitud);

        return ejecutarAccionGemini(
                solicitud,
                TipoAccionIa.RESUMEN,
                instruccion,
                ip,
                navegador);
    }

    /**
     * Traduce un mensaje al idioma solicitado.
     *
     * @param solicitud mensaje e idioma destino
     * @param ip direccion IP de la peticion
     * @param navegador navegador o cliente usado
     * @return traduccion generada por Gemini
     */
    public RespuestaGeminiDTO traducir(
            SolicitudGeminiDTO solicitud,
            String ip,
            String navegador) {

        validarTexto(
                solicitud.getMensaje(),
                "El mensaje a traducir no puede estar vacio",
                MAXIMO_CARACTERES_MENSAJE);

        validarTexto(
                solicitud.getIdiomaDestino(),
                "Debes indicar el idioma destino",
                80);

        String instruccion =
                construirInstruccionTraduccion(solicitud);

        return ejecutarAccionGemini(
                solicitud,
                TipoAccionIa.TRADUCCION,
                instruccion,
                ip,
                navegador);
    }

    /**
     * Obtiene el historial reciente del usuario autenticado.
     *
     * @return ultimas interacciones de IA sin texto sensible completo
     */
    public List<HistorialIaDTO> obtenerHistorial() {

        Usuario usuario =
                seguridadIaService.obtenerUsuarioAutenticado();

        List<InteraccionIa> interacciones =
                interaccionIaRepository
                        .findTop20ByUsuario_IdOrderByFechaCreacionDesc(
                                usuario.getId());

        List<HistorialIaDTO> historial =
                new ArrayList<>();

        for (InteraccionIa interaccion : interacciones) {
            historial.add(
                    new HistorialIaDTO(
                            interaccion.getId(),
                            interaccion.getTipoAccion(),
                            interaccion.getConversacionId(),
                            interaccion.getVistaPreviaSolicitud(),
                            interaccion.getVistaPreviaRespuesta(),
                            interaccion.isExitoso(),
                            interaccion.getFechaCreacion()));
        }

        return historial;
    }

    private RespuestaGeminiDTO ejecutarAccionGemini(
            SolicitudGeminiDTO solicitud,
            TipoAccionIa tipoAccion,
            String instruccion,
            String ip,
            String navegador) {

        Usuario usuario =
                seguridadIaService.obtenerUsuarioAutenticado();

        seguridadIaService.validarAccesoConversacion(
                solicitud.getConversacionId(),
                usuario);

        LocalDateTime fechaCreacion =
                LocalDateTime.now();

        try {
            String respuesta =
                    consultarGemini(instruccion);

            guardarInteraccion(
                    usuario,
                    solicitud,
                    tipoAccion,
                    instruccion,
                    respuesta,
                    true,
                    null,
                    fechaCreacion);

            auditoriaService.registrarUsoIa(
                    usuario,
                    tipoAccion,
                    solicitud,
                    ip,
                    navegador,
                    true);

            return new RespuestaGeminiDTO(
                    respuesta,
                    tipoAccion,
                    solicitud.getConversacionId(),
                    fechaCreacion);

        } catch (RuntimeException e) {

            guardarInteraccion(
                    usuario,
                    solicitud,
                    tipoAccion,
                    instruccion,
                    null,
                    false,
                    e.getMessage(),
                    fechaCreacion);

            auditoriaService.registrarUsoIa(
                    usuario,
                    tipoAccion,
                    solicitud,
                    ip,
                    navegador,
                    false);

            throw e;
        }
    }

    private String consultarGemini(
            String instruccion) {

        if (apiKey == null
                || apiKey.trim().isEmpty()) {

            throw new IllegalStateException(
                    "GEMINI_API_KEY no esta configurada en el backend");
        }

        try {
            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(construirUrlGemini()))
                            .timeout(Duration.ofSeconds(45))
                            .header("Content-Type", "application/json")
                            .header("x-goog-api-key", apiKey.trim())
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    construirCuerpoGemini(instruccion)))
                            .build();

            HttpResponse<String> respuestaHttp =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString());

            if (respuestaHttp.statusCode() < 200
                    || respuestaHttp.statusCode() >= 300) {

                throw new IllegalStateException(
                        extraerErrorGemini(respuestaHttp.body()));
            }

            return extraerTextoGemini(
                    respuestaHttp.body());

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "La solicitud a Gemini fue interrumpida",
                    e);

        } catch (IllegalStateException e) {

            throw e;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "No fue posible comunicarse con Gemini",
                    e);
        }
    }

    private String construirUrlGemini() {

        String urlBaseLimpia =
                baseUrl.endsWith("/")
                        ? baseUrl.substring(0, baseUrl.length() - 1)
                        : baseUrl;

        String modeloLimpio =
                model.startsWith("models/")
                        ? model
                        : "models/" + model;

        return urlBaseLimpia
                + "/v1beta/"
                + modeloLimpio
                + ":generateContent";
    }

    private String construirCuerpoGemini(
            String instruccion) {

        JsonObject raiz =
                new JsonObject();

        JsonObject instruccionSistema =
                new JsonObject();

        JsonArray partesSistema =
                new JsonArray();

        JsonObject textoSistema =
                new JsonObject();

        textoSistema.addProperty(
                "text",
                "Eres Asistente IA de un WhatsApp academico. "
                        + "Responde en espanol claro, breve y util. "
                        + "No pidas contrasenas ni frases secretas. "
                        + "Si falta contexto, dilo y pide solo la informacion necesaria.");

        partesSistema.add(textoSistema);
        instruccionSistema.add("parts", partesSistema);
        raiz.add("systemInstruction", instruccionSistema);

        JsonArray contenidos =
                new JsonArray();

        JsonObject contenido =
                new JsonObject();

        contenido.addProperty("role", "user");

        JsonArray partes =
                new JsonArray();

        JsonObject texto =
                new JsonObject();

        texto.addProperty("text", instruccion);
        partes.add(texto);
        contenido.add("parts", partes);
        contenidos.add(contenido);
        raiz.add("contents", contenidos);

        JsonObject configuracionGeneracion =
                new JsonObject();

        configuracionGeneracion.addProperty("temperature", 0.4);
        configuracionGeneracion.addProperty("maxOutputTokens", 800);
        raiz.add("generationConfig", configuracionGeneracion);

        return raiz.toString();
    }

    private String extraerTextoGemini(
            String cuerpoRespuesta) {

        JsonObject raiz =
                JsonParser
                        .parseString(cuerpoRespuesta)
                        .getAsJsonObject();

        JsonArray candidatos =
                raiz.getAsJsonArray("candidates");

        if (candidatos == null
                || candidatos.isEmpty()) {

            throw new IllegalStateException(
                    "Gemini no genero una respuesta");
        }

        JsonObject contenido =
                candidatos
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("content");

        if (contenido == null
                || !contenido.has("parts")) {

            throw new IllegalStateException(
                    "La respuesta de Gemini no tiene contenido");
        }

        JsonArray partes =
                contenido.getAsJsonArray("parts");

        StringBuilder respuesta =
                new StringBuilder();

        for (JsonElement parte : partes) {
            JsonObject parteObjeto =
                    parte.getAsJsonObject();

            if (parteObjeto.has("text")) {
                respuesta.append(
                        parteObjeto
                                .get("text")
                                .getAsString());
            }
        }

        String texto =
                respuesta.toString().trim();

        if (texto.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini devolvio una respuesta vacia");
        }

        return texto;
    }

    private String extraerErrorGemini(
            String cuerpoRespuesta) {

        try {
            JsonObject raiz =
                    JsonParser
                            .parseString(cuerpoRespuesta)
                            .getAsJsonObject();

            JsonObject error =
                    raiz.getAsJsonObject("error");

            if (error != null
                    && error.has("message")) {

                return "Gemini rechazo la solicitud: "
                        + error.get("message")
                                .getAsString();
            }

        } catch (Exception ignored) {
        }

        return "Gemini rechazo la solicitud";
    }

    private String construirInstruccionConsulta(
            SolicitudGeminiDTO solicitud) {

        StringBuilder instruccion =
                new StringBuilder();

        agregarContexto(instruccion, solicitud.getContexto());

        instruccion.append("Pregunta del usuario:\n")
                .append(solicitud.getMensaje());

        return instruccion.toString();
    }

    private String construirInstruccionSugerencia(
            SolicitudGeminiDTO solicitud) {

        StringBuilder instruccion =
                new StringBuilder();

        agregarContexto(instruccion, solicitud.getContexto());

        instruccion.append("Genera 3 respuestas sugeridas para este mensaje. ")
                .append("Deben sonar naturales, respetuosas y academicas.\n\n")
                .append("Mensaje:\n")
                .append(solicitud.getMensaje());

        return instruccion.toString();
    }

    private String construirInstruccionResumen(
            SolicitudGeminiDTO solicitud) {

        StringBuilder instruccion =
                new StringBuilder();

        instruccion.append("Resume esta conversacion en maximo 6 puntos. ")
                .append("Incluye acuerdos, pendientes y decisiones si existen.\n\n")
                .append("Texto autorizado por el usuario:\n")
                .append(solicitud.getContexto());

        if (tieneTexto(solicitud.getMensaje())) {
            instruccion.append("\n\nInstruccion adicional:\n")
                    .append(solicitud.getMensaje());
        }

        return instruccion.toString();
    }

    private String construirInstruccionTraduccion(
            SolicitudGeminiDTO solicitud) {

        return "Traduce el siguiente mensaje al idioma "
                + solicitud.getIdiomaDestino()
                + ". Devuelve solo la traduccion, sin explicaciones.\n\n"
                + solicitud.getMensaje();
    }

    private void agregarContexto(
            StringBuilder instruccion,
            String contexto) {

        if (!tieneTexto(contexto)) {
            return;
        }

        validarTexto(
                contexto,
                "El contexto no puede estar vacio",
                MAXIMO_CARACTERES_CONTEXTO);

        instruccion.append("Contexto autorizado por el usuario:\n")
                .append(contexto)
                .append("\n\n");
    }

    private void validarTexto(
            String valor,
            String mensajeVacio,
            int maximoCaracteres) {

        if (!tieneTexto(valor)) {
            throw new IllegalArgumentException(
                    mensajeVacio);
        }

        if (valor.length() > maximoCaracteres) {
            throw new IllegalArgumentException(
                    "El texto supera el limite permitido de "
                            + maximoCaracteres
                            + " caracteres");
        }
    }

    private boolean tieneTexto(
            String valor) {

        return valor != null
                && !valor.trim().isEmpty();
    }

    private void guardarInteraccion(
            Usuario usuario,
            SolicitudGeminiDTO solicitud,
            TipoAccionIa tipoAccion,
            String instruccion,
            String respuesta,
            boolean exitoso,
            String mensajeError,
            LocalDateTime fechaCreacion) {

        InteraccionIa interaccion =
                new InteraccionIa();

        interaccion.setUsuario(usuario);
        interaccion.setTipoAccion(tipoAccion);
        interaccion.setConversacionId(
                solicitud.getConversacionId());
        interaccion.setHashSolicitud(
                sha256(instruccion));
        interaccion.setVistaPreviaSolicitud(
                recortar(instruccion, 160));
        interaccion.setVistaPreviaRespuesta(
                recortar(respuesta, 220));
        interaccion.setExitoso(exitoso);
        interaccion.setMensajeError(
                recortar(mensajeError, 250));
        interaccion.setFechaCreacion(fechaCreacion);

        interaccionIaRepository.save(interaccion);
    }

    private String sha256(
            String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No fue posible calcular hash de auditoria",
                    e);
        }
    }

    private String recortar(
            String valor,
            int maximo) {

        if (valor == null) {
            return null;
        }

        if (valor.length() <= maximo) {
            return valor;
        }

        return valor.substring(0, maximo);
    }
}
