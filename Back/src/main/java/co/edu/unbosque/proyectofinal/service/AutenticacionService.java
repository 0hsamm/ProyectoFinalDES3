package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.LoginDTO;
import co.edu.unbosque.proyectofinal.dto.RegistroDTO;
import co.edu.unbosque.proyectofinal.entity.TokenVerificacion;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.CorreoYaExisteException;
import co.edu.unbosque.proyectofinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.proyectofinal.exception.UsuarioNoHabilitadoException;
import co.edu.unbosque.proyectofinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.proyectofinal.exception.ValidadorUsuario;
import co.edu.unbosque.proyectofinal.repository.TokenVerificacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class AutenticacionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AutenticacionService.class);

    private static final String SOBRE_MI_POR_DEFECTO =
            "Reload, lo mejor!";

    private final UsuarioRepository usuarioRepo;

    private final TokenVerificacionRepository tokenRepo;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public AutenticacionService(
            UsuarioRepository usuarioRepo,
            TokenVerificacionRepository tokenRepo,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.usuarioRepo = usuarioRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

	public RegistroResultado registrar(RegistroDTO dto) {

        return registrar(
                dto,
                null);
    }

	public RegistroResultado registrar(
            RegistroDTO dto,
            String origenFrontend) {

		ValidadorUsuario.verificarUsuario(
				dto.getUsuario());

		ValidadorUsuario.verificarCorreo(
				dto.getCorreo());

		ValidadorUsuario.verificarContrasena(
				dto.getContrasena());

		ValidadorUsuario.verificarEdad(
				dto.getFechaNacimiento());

		Optional<Usuario> usuarioExistente =
				usuarioRepo.findByUsuario(
						dto.getUsuario());

		if (usuarioExistente.isPresent()) {
			throw new UsuarioYaExisteException();
		}

		Optional<Usuario> correoExistente =
				usuarioRepo.findByCorreo(
						dto.getCorreo());

		if (correoExistente.isPresent()) {
			throw new CorreoYaExisteException();
		}

		Usuario usuario =
	            new Usuario();

	    usuario.setUsuario(
	            dto.getUsuario());

	    usuario.setCorreo(
	            dto.getCorreo());

	    usuario.setHabilitado(false);

	    usuario.setNombrePersona(dto.getNombrePersona());
	    usuario.setContrasenaHash(
	        passwordEncoder.encode(dto.getContrasena()));
        usuario.setSobreMi(SOBRE_MI_POR_DEFECTO);
	    usuario.setFechaNacimiento(dto.getFechaNacimiento());
	    usuario.setFechaCreacionCuenta(LocalDateTime.now());
	    usuario.setEnLinea(false);
	    usuario.setMostrarEnLinea(true);
	    usuario.setRol(co.edu.unbosque.proyectofinal.enums.RolUsuario.ROLE_USER);
	    
	    usuarioRepo.save(usuario);

	    String token =
	            UUID.randomUUID().toString();

	    TokenVerificacion tv =
	            new TokenVerificacion();

	    tv.setToken(token);

	    tv.setUsuario(usuario);

	    tv.setFechaExpiracion(
	            LocalDateTime.now().plusHours(24));

	    tokenRepo.save(tv);

        boolean correoEnviado =
                enviarCorreoVerificacionSeguro(
                        usuario.getCorreo(),
                        token,
                        origenFrontend);

	    return new RegistroResultado(
                usuario,
                correoEnviado);
	}

	public Usuario login(LoginDTO dto) {

		Optional<Usuario> usuario =
				usuarioRepo.findByUsuario(
						dto.getUsuario());

		if (usuario.isEmpty()) {
			throw new CredencialesInvalidasException();
		}

		Usuario user =
				usuario.get();

		if (!contrasenaValida(
				dto.getContrasena(),
				user.getContrasenaHash())) {

			throw new CredencialesInvalidasException();
		}

		if (!user.getContrasenaHash()
				.startsWith("$2")) {

			user.setContrasenaHash(
					passwordEncoder.encode(
							dto.getContrasena()));
		}if (!user.isHabilitado()) {
		    throw new UsuarioNoHabilitadoException();
		}
		

		user.setEnLinea(true);

		user.setUltimaVezEnLinea(
				LocalDateTime.now());

		usuarioRepo.save(user);

		return user;
	}

	private boolean contrasenaValida(
			String contrasena,
			String contrasenaGuardada) {

		if (contrasenaGuardada == null) {
			return false;
		}

		if (contrasenaGuardada.startsWith("$2")) {
			return passwordEncoder.matches(
					contrasena,
					contrasenaGuardada);
		}

		return contrasenaGuardada.equals(
				contrasena);
	}

	public boolean verificarCuenta(
			String token) {

		Optional<TokenVerificacion> tv =
				tokenRepo.findByToken(token);

		if (!tv.isPresent()) {
			return false;
		}

		TokenVerificacion tokenVerificacion =
				tv.get();

		 if (tokenVerificacion
		            .getFechaExpiracion()
		            .isBefore(LocalDateTime.now())) {

		        tokenRepo.delete(tokenVerificacion);

		        return false;
		    }
		
		Usuario usuario =
				tokenVerificacion.getUsuario();

		usuario.setHabilitado(true);

		usuarioRepo.save(usuario);
		tokenRepo.delete(tokenVerificacion);

		return true;
	}
	public Optional<Usuario> buscarPorCorreo(String correo) {
	    return usuarioRepo.findByCorreo(correo);
	}

	public void guardar(Usuario usuario) {
	    usuarioRepo.save(usuario);
	}

    private boolean enviarCorreoVerificacionSeguro(
            String correo,
            String token,
            String origenFrontend) {

        try {
            emailService.enviarCorreoVerificacion(
                    correo,
                    token,
                    origenFrontend);

            return true;

        } catch (MailAuthenticationException e) {
            LOGGER.warn(
                    "No se pudo autenticar el SMTP para enviar el correo a {}. "
                            + "Configura SPRING_MAIL_USERNAME y un app password valido.",
                    correo,
                    e);

            return false;

        } catch (MailException e) {
            LOGGER.warn(
                    "No se pudo enviar el correo de verificacion a {}",
                    correo,
                    e);

            return false;

        } catch (RuntimeException e) {
            LOGGER.warn(
                    "No se pudo enviar el correo de verificacion a {}",
                    correo,
                    e);

            return false;
        }
    }

    public record RegistroResultado(
            Usuario usuario,
            boolean correoEnviado) {
    }

}
