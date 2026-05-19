package co.edu.unbosque.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.proyectofinal.dto.LoginDTO;
import co.edu.unbosque.proyectofinal.dto.RegistroDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.CorreoYaExisteException;
import co.edu.unbosque.proyectofinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.proyectofinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.proyectofinal.repository.TokenVerificacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private TokenVerificacionRepository tokenRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticacionService autenticacionService;

    @Test
    void registrarCreaUsuarioConContrasenaCifrada() {
        RegistroDTO dto =
                registroValido();

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.empty());
        when(usuarioRepo.findByCorreo("usuario@correo.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678"))
                .thenReturn("hash-seguro");

        Usuario creado =
                autenticacionService.registrar(dto);

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepo).save(captor.capture());

        Usuario guardado =
                captor.getValue();

        assertEquals("usuario1", guardado.getUsuario());
        assertEquals("usuario@correo.com", guardado.getCorreo());
        assertEquals("hash-seguro", guardado.getContrasenaHash());
        assertEquals("Hola! Estoy usando WZ", guardado.getSobreMi());
        assertNotNull(guardado.getFechaCreacionCuenta());
        assertEquals(creado, guardado);
    }

    @Test
    void registrarRechazaUsuarioDuplicado() {
        RegistroDTO dto =
                registroValido();

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                UsuarioYaExisteException.class,
                () -> autenticacionService.registrar(dto));

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    void registrarRechazaCorreoDuplicado() {
        RegistroDTO dto =
                registroValido();

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.empty());
        when(usuarioRepo.findByCorreo("usuario@correo.com"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                CorreoYaExisteException.class,
                () -> autenticacionService.registrar(dto));

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    void loginAceptaContrasenaBCrypt() {
        LoginDTO dto =
                loginValido();

        Usuario usuario =
                usuarioConContrasena("$2hash");

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("12345678", "$2hash"))
                .thenReturn(true);

        Usuario resultado =
                autenticacionService.login(dto);

        assertTrue(resultado.isEnLinea());
        assertNotNull(resultado.getUltimaVezEnLinea());
        verify(passwordEncoder, never()).encode("12345678");
        verify(usuarioRepo).save(usuario);
    }

    @Test
    void loginMigraContrasenaAntiguaEnTextoPlano() {
        LoginDTO dto =
                loginValido();

        Usuario usuario =
                usuarioConContrasena("12345678");

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("12345678"))
                .thenReturn("$2hash-nuevo");

        autenticacionService.login(dto);

        assertEquals("$2hash-nuevo", usuario.getContrasenaHash());
        verify(usuarioRepo).save(usuario);
    }

    @Test
    void loginRechazaCredencialesInvalidas() {
        LoginDTO dto =
                loginValido();

        when(usuarioRepo.findByUsuario("usuario1"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> autenticacionService.login(dto));
    }

    private RegistroDTO registroValido() {
        RegistroDTO dto =
                new RegistroDTO();

        dto.setUsuario("usuario1");
        dto.setCorreo("usuario@correo.com");
        dto.setNombrePersona("Usuario Prueba");
        dto.setContrasena("12345678");
        dto.setFechaNacimiento(
                LocalDate.now().minusYears(20));

        return dto;
    }

    private LoginDTO loginValido() {
        LoginDTO dto =
                new LoginDTO();

        dto.setUsuario("usuario1");
        dto.setContrasena("12345678");

        return dto;
    }

    private Usuario usuarioConContrasena(
            String contrasena) {

        Usuario usuario =
                new Usuario();

        usuario.setUsuario("usuario1");
        usuario.setCorreo("usuario@correo.com");
        usuario.setContrasenaHash(contrasena);

        return usuario;
    }
}
