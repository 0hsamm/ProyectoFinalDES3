package co.edu.unbosque.proyectofinal.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.unbosque.proyectofinal.dto.HistorialIaDTO;
import co.edu.unbosque.proyectofinal.dto.RespuestaGeminiDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import co.edu.unbosque.proyectofinal.service.AsistenteIaService;

@ExtendWith(MockitoExtension.class)
class AsistenteIaControllerTest {

    @Mock
    private AsistenteIaService asistenteIaService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new AsistenteIaController(asistenteIaService))
                        .build();
    }

    @Test
    void chatRetornaRespuestaGeneradaPorIa()
            throws Exception {

        RespuestaGeminiDTO respuesta =
                new RespuestaGeminiDTO(
                        "Hola, soy el Asistente IA",
                        TipoAccionIa.CONSULTA,
                        7L,
                        LocalDateTime.of(2026, 5, 19, 8, 0));

        when(asistenteIaService.consultar(
                any(SolicitudGeminiDTO.class),
                eq("10.0.0.15"),
                eq("JUnit")))
                .thenReturn(respuesta);

        mockMvc.perform(post("/api/ia/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", "10.0.0.15, 10.0.0.20")
                        .header("User-Agent", "JUnit")
                        .content("""
                                {
                                  "mensaje": "Hola IA",
                                  "conversacionId": 7
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta")
                        .value("Hola, soy el Asistente IA"))
                .andExpect(jsonPath("$.tipoAccion")
                        .value("CONSULTA"))
                .andExpect(jsonPath("$.conversacionId")
                        .value(7));

        verify(asistenteIaService)
                .consultar(
                        any(SolicitudGeminiDTO.class),
                        eq("10.0.0.15"),
                        eq("JUnit"));
    }

    @Test
    void traducirAceptaAliasEnIngles()
            throws Exception {

        RespuestaGeminiDTO respuesta =
                new RespuestaGeminiDTO(
                        "Good morning",
                        TipoAccionIa.TRADUCCION,
                        4L,
                        LocalDateTime.now());

        when(asistenteIaService.traducir(
                any(SolicitudGeminiDTO.class),
                any(),
                any()))
                .thenReturn(respuesta);

        mockMvc.perform(post("/api/ai/translate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Buenos dias",
                                  "conversationId": 4,
                                  "targetLanguage": "ingles"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta")
                        .value("Good morning"))
                .andExpect(jsonPath("$.tipoAccion")
                        .value("TRADUCCION"));
    }

    @Test
    void sugerirRespuestaRetornaBadRequestSiFaltaMensaje()
            throws Exception {

        when(asistenteIaService.sugerirRespuesta(
                any(SolicitudGeminiDTO.class),
                any(),
                any()))
                .thenThrow(
                        new IllegalArgumentException(
                                "El mensaje base no puede estar vacio"));

        mockMvc.perform(post("/api/ia/sugerir-respuesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("El mensaje base no puede estar vacio"));
    }

    @Test
    void resumirRetornaForbiddenSiUsuarioNoTienePermiso()
            throws Exception {

        when(asistenteIaService.resumir(
                any(SolicitudGeminiDTO.class),
                any(),
                any()))
                .thenThrow(
                        new AccessDeniedException(
                                "No tienes permiso para esta conversacion"));

        mockMvc.perform(post("/api/ia/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "contexto": "Texto autorizado",
                                  "conversacionId": 99
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("No tienes permiso para esta conversacion"));
    }

    @Test
    void historialRetornaInteraccionesRecientes()
            throws Exception {

        HistorialIaDTO historial =
                new HistorialIaDTO(
                        1L,
                        TipoAccionIa.RESUMEN,
                        12L,
                        "Resumen",
                        "Respuesta",
                        true,
                        LocalDateTime.now());

        when(asistenteIaService.obtenerHistorial())
                .thenReturn(List.of(historial));

        mockMvc.perform(get("/api/ia/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoAccion")
                        .value("RESUMEN"))
                .andExpect(jsonPath("$[0].conversacionId")
                        .value(12));
    }
}
