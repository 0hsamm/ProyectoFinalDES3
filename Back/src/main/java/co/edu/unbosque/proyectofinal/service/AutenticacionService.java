package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.LoginDTO;
import co.edu.unbosque.proyectofinal.dto.RegistroDTO;
import co.edu.unbosque.proyectofinal.entity.TokenVerificacion;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.CorreoYaExisteException;
import co.edu.unbosque.proyectofinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.proyectofinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.proyectofinal.exception.ValidadorUsuario;
import co.edu.unbosque.proyectofinal.repository.TokenVerificacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class AutenticacionService {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private TokenVerificacionRepository tokenRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService eService;

	public Usuario registrar(RegistroDTO dto) {

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
	    usuario.setFechaNacimiento(dto.getFechaNacimiento());
	    usuario.setFechaCreacionCuenta(LocalDateTime.now());
	    usuario.setEnLinea(false);
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

	    eService.enviarCorreoVerificacion(
	            usuario.getCorreo(),
	            token);

	    return usuario;
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

		    throw new CredencialesInvalidasException();
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

		        return false;
		    }
		
		Usuario usuario =
				tokenVerificacion.getUsuario();

		usuario.setHabilitado(true);

		usuarioRepo.save(usuario);

		return true;
	}
	
	
}
