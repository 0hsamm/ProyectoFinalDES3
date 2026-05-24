package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.ParticipanteConversacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class SeguridadIaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ParticipanteConversacionRepository participanteRepository;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void obtenerUsuarioAutenticadoRetornaPrincipalUsuario() {
        Usuario usuario =
                new Usuario();

        usuario.setId(1L);
        usuario.setCorreo("usuario@correo.com");

        UsernamePasswordAuthenticationToken autenticacion =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        Collections.emptyList());

        SecurityContextHolder
                .getContext()
                .setAuthentication(autenticacion);

        SeguridadIaService service =
                new SeguridadIaService(
                        usuarioRepository,
                        participanteRepository);

        assertEquals(
                usuario,
                service.obtenerUsuarioAutenticado());
    }

    @Test
    void obtenerUsuarioAutenticadoBuscaPorCorreoCuandoPrincipalEsString() {
        Usuario usuario =
                new Usuario();

        usuario.setId(2L);
        usuario.setCorreo("usuario@correo.com");

        UsernamePasswordAuthenticationToken autenticacion =
                new UsernamePasswordAuthenticationToken(
                        "usuario@correo.com",
                        null,
                        Collections.emptyList());

        SecurityContextHolder
                .getContext()
                .setAuthentication(autenticacion);

        when(usuarioRepository.findByCorreo("usuario@correo.com"))
                .thenReturn(Optional.of(usuario));

        SeguridadIaService service =
                new SeguridadIaService(
                        usuarioRepository,
                        participanteRepository);

        assertEquals(
                usuario,
                service.obtenerUsuarioAutenticado());
    }

    @Test
    void validarAccesoConversacionRechazaSiNoEsParticipante() {
        Usuario usuario =
                new Usuario();

        usuario.setId(5L);

        when(participanteRepository
                .existsByConversacion_IdAndUsuario_Id(10L, 5L))
                .thenReturn(false);

        SeguridadIaService service =
                new SeguridadIaService(
                        usuarioRepository,
                        participanteRepository);

        assertThrows(
                AccessDeniedException.class,
                () -> service.validarAccesoConversacion(10L, usuario));
    }
}
