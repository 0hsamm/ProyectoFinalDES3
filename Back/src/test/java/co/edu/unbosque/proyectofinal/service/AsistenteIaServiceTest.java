package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.proyectofinal.dto.HistorialIaDTO;
import co.edu.unbosque.proyectofinal.dto.SolicitudGeminiDTO;
import co.edu.unbosque.proyectofinal.entity.InteraccionIa;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.TipoAccionIa;
import co.edu.unbosque.proyectofinal.repository.InteraccionIaRepository;

@ExtendWith(MockitoExtension.class)
class AsistenteIaServiceTest {

    @Mock
    private SeguridadIaService seguridadIaService;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private InteraccionIaRepository interaccionIaRepository;

    @Test
    void consultarRechazaSolicitudNulaAntesDeTocarDependencias() {
        AsistenteIaService service =
                crearService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar(
                        null,
                        "127.0.0.1",
                        "JUnit"));

        verifyNoInteractions(
                seguridadIaService,
                auditoriaService,
                interaccionIaRepository);
    }

    @Test
    void consultarConApiKeyFaltanteRegistraInteraccionFallida() {
        Usuario usuario =
                new Usuario();

        usuario.setId(3L);

        SolicitudGeminiDTO solicitud =
                new SolicitudGeminiDTO();

        solicitud.setMensaje("Hola IA");
        solicitud.setConversacionId(9L);
        solicitud.setUbicacion("Bogota");

        when(seguridadIaService.obtenerUsuarioAutenticado())
                .thenReturn(usuario);

        AsistenteIaService service =
                crearService();

        assertThrows(
                IllegalStateException.class,
                () -> service.consultar(
                        solicitud,
                        "127.0.0.1",
                        "JUnit"));

        ArgumentCaptor<InteraccionIa> captor =
                ArgumentCaptor.forClass(InteraccionIa.class);

        verify(interaccionIaRepository)
                .save(captor.capture());

        InteraccionIa interaccion =
                captor.getValue();

        assertEquals(TipoAccionIa.CONSULTA, interaccion.getTipoAccion());
        assertEquals(9L, interaccion.getConversacionId());
        assertFalse(interaccion.isExitoso());
        assertNotNull(interaccion.getHashSolicitud());
        assertNotNull(interaccion.getFechaCreacion());

        verify(seguridadIaService)
                .validarAccesoConversacion(9L, usuario);

        verify(auditoriaService)
                .registrarUsoIa(
                        eq(usuario),
                        eq(TipoAccionIa.CONSULTA),
                        eq(solicitud),
                        eq("127.0.0.1"),
                        eq("JUnit"),
                        eq(false));
    }

    @Test
    void obtenerHistorialMapeaInteraccionesDelUsuario() {
        Usuario usuario =
                new Usuario();

        usuario.setId(11L);

        InteraccionIa interaccion =
                new InteraccionIa();

        interaccion.setId(1L);
        interaccion.setTipoAccion(TipoAccionIa.RESUMEN);
        interaccion.setConversacionId(22L);
        interaccion.setVistaPreviaSolicitud("Resumen");
        interaccion.setVistaPreviaRespuesta("Respuesta");
        interaccion.setExitoso(true);
        interaccion.setFechaCreacion(LocalDateTime.now());

        when(seguridadIaService.obtenerUsuarioAutenticado())
                .thenReturn(usuario);
        when(interaccionIaRepository
                .findTop20ByUsuario_IdOrderByFechaCreacionDesc(11L))
                .thenReturn(List.of(interaccion));

        AsistenteIaService service =
                crearService();

        List<HistorialIaDTO> historial =
                service.obtenerHistorial();

        assertEquals(1, historial.size());
        assertEquals(TipoAccionIa.RESUMEN, historial.get(0).getTipoAccion());
        assertEquals(22L, historial.get(0).getConversacionId());
        assertEquals("Resumen", historial.get(0).getVistaPreviaSolicitud());
    }

    private AsistenteIaService crearService() {
        return new AsistenteIaService(
                seguridadIaService,
                auditoriaService,
                interaccionIaRepository);
    }
}
