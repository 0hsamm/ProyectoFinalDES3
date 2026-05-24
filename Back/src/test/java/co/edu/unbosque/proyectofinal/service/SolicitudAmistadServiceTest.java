package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.proyectofinal.dto.AmistadDTO;
import co.edu.unbosque.proyectofinal.dto.CrearSolicitudAmistadDTO;
import co.edu.unbosque.proyectofinal.entity.SolicitudAmistad;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;
import co.edu.unbosque.proyectofinal.repository.SolicitudAmistadRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudAmistadServiceTest {

    @Mock
    private SolicitudAmistadRepository solicitudAmistadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitudAmistadService solicitudAmistadService;

    @Test
    void enviarSolicitudCreaRelacionPendiente() {
        Usuario solicitante =
                usuario(1L, "ana", "ana@correo.com", "Ana");
        Usuario receptor =
                usuario(2L, "maria", "maria@correo.com", "Maria");
        CrearSolicitudAmistadDTO dto =
                new CrearSolicitudAmistadDTO("maria");

        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(solicitante));
        when(usuarioRepository.findByUsuario("maria"))
                .thenReturn(Optional.of(receptor));
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(1L, 2L))
                .thenReturn(Optional.empty());
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(2L, 1L))
                .thenReturn(Optional.empty());

        int resultado =
                solicitudAmistadService.enviarSolicitud(
                        "ana@correo.com",
                        dto);

        ArgumentCaptor<SolicitudAmistad> captor =
                ArgumentCaptor.forClass(
                        SolicitudAmistad.class);

        verify(solicitudAmistadRepository)
                .save(captor.capture());

        SolicitudAmistad guardada =
                captor.getValue();

        assertEquals(0, resultado);
        assertEquals(
                EstadoSolicitudAmistad.PENDIENTE,
                guardada.getEstado());
        assertEquals(solicitante, guardada.getSolicitante());
        assertEquals(receptor, guardada.getReceptor());
        assertNotNull(guardada.getFechaSolicitud());
    }

    @Test
    void enviarSolicitudReutilizaRelacionRechazada() {
        Usuario solicitante =
                usuario(1L, "ana", "ana@correo.com", "Ana");
        Usuario receptor =
                usuario(2L, "maria", "maria@correo.com", "Maria");
        SolicitudAmistad solicitud =
                new SolicitudAmistad();

        solicitud.setSolicitante(solicitante);
        solicitud.setReceptor(receptor);
        solicitud.setEstado(
                EstadoSolicitudAmistad.RECHAZADA);
        solicitud.setFechaSolicitud(
                LocalDateTime.now().minusDays(2));
        solicitud.setFechaRespuesta(
                LocalDateTime.now().minusDays(1));

        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(solicitante));
        when(usuarioRepository.findByUsuario("maria"))
                .thenReturn(Optional.of(receptor));
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(1L, 2L))
                .thenReturn(Optional.of(solicitud));

        int resultado =
                solicitudAmistadService.enviarSolicitud(
                        "ana@correo.com",
                        new CrearSolicitudAmistadDTO("maria"));

        assertEquals(0, resultado);
        assertEquals(
                EstadoSolicitudAmistad.PENDIENTE,
                solicitud.getEstado());
        assertFalse(
                solicitud.getFechaSolicitud()
                        .isBefore(
                                LocalDateTime.now().minusMinutes(1)));
        assertNull(solicitud.getFechaRespuesta());
        verify(solicitudAmistadRepository)
                .save(solicitud);
    }

    @Test
    void aceptarSolicitudMarcaRelacionComoAceptada() {
        Usuario solicitante =
                usuario(1L, "ana", "ana@correo.com", "Ana");
        Usuario receptor =
                usuario(2L, "maria", "maria@correo.com", "Maria");
        SolicitudAmistad solicitud =
                new SolicitudAmistad();

        solicitud.setId(15L);
        solicitud.setSolicitante(solicitante);
        solicitud.setReceptor(receptor);
        solicitud.setEstado(
                EstadoSolicitudAmistad.PENDIENTE);
        solicitud.setFechaSolicitud(
                LocalDateTime.now().minusHours(3));

        when(usuarioRepository.findByCorreo("maria@correo.com"))
                .thenReturn(Optional.of(receptor));
        when(solicitudAmistadRepository.findById(15L))
                .thenReturn(Optional.of(solicitud));

        int resultado =
                solicitudAmistadService.aceptarSolicitud(
                        "maria@correo.com",
                        15L);

        assertEquals(0, resultado);
        assertEquals(
                EstadoSolicitudAmistad.ACEPTADA,
                solicitud.getEstado());
        assertNotNull(solicitud.getFechaRespuesta());
        verify(solicitudAmistadRepository)
                .save(solicitud);
    }

    @Test
    void obtenerAmigosUneRelacionesAceptadasDeAmbosLados() {
        Usuario actual =
                usuario(1L, "ana", "ana@correo.com", "Ana");
        Usuario amigoUno =
                usuario(2L, "maria", "maria@correo.com", "Maria");
        Usuario amigoDos =
                usuario(3L, "carlos", "carlos@correo.com", "Carlos");
        SolicitudAmistad directa =
                new SolicitudAmistad(
                        actual,
                        amigoUno,
                        EstadoSolicitudAmistad.ACEPTADA,
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(2));
        SolicitudAmistad inversa =
                new SolicitudAmistad(
                        amigoDos,
                        actual,
                        EstadoSolicitudAmistad.ACEPTADA,
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1));

        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(actual));
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndEstado(
                        1L,
                        EstadoSolicitudAmistad.ACEPTADA))
                .thenReturn(List.of(directa));
        when(solicitudAmistadRepository
                .findByReceptor_IdAndEstado(
                        1L,
                        EstadoSolicitudAmistad.ACEPTADA))
                .thenReturn(List.of(inversa));

        List<AmistadDTO> amigos =
                solicitudAmistadService.obtenerAmigos(
                        "ana@correo.com");

        assertEquals(2, amigos.size());
        assertEquals("carlos", amigos.get(0).getUsuario());
        assertEquals("maria", amigos.get(1).getUsuario());
        assertTrue(
                amigos.stream()
                        .anyMatch(amigo ->
                                amigo.getUsuarioId().equals(2L)));
        assertTrue(
                amigos.stream()
                        .anyMatch(amigo ->
                                amigo.getUsuarioId().equals(3L)));
    }

    @Test
    void eliminarAmistadBorraRelacionAceptadaInversa() {
        Usuario actual =
                usuario(1L, "ana", "ana@correo.com", "Ana");
        Usuario amigo =
                usuario(2L, "maria", "maria@correo.com", "Maria");
        SolicitudAmistad relacion =
                new SolicitudAmistad();

        relacion.setSolicitante(amigo);
        relacion.setReceptor(actual);
        relacion.setEstado(
                EstadoSolicitudAmistad.ACEPTADA);

        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(actual));
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(1L, 2L))
                .thenReturn(Optional.empty());
        when(solicitudAmistadRepository
                .findBySolicitante_IdAndReceptor_Id(2L, 1L))
                .thenReturn(Optional.of(relacion));

        int resultado =
                solicitudAmistadService.eliminarAmistad(
                        "ana@correo.com",
                        2L);

        assertEquals(0, resultado);
        verify(solicitudAmistadRepository)
                .delete(relacion);
    }

    private Usuario usuario(
            Long id,
            String username,
            String correo,
            String nombre) {

        Usuario usuario =
                new Usuario();

        usuario.setId(id);
        usuario.setUsuario(username);
        usuario.setCorreo(correo);
        usuario.setNombrePersona(nombre);
        usuario.setSobreMi("Hola");
        usuario.setEnLinea(true);
        usuario.setHabilitado(true);

        return usuario;
    }
}
