package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.Optional;

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

		Usuario nuevo =
				new Usuario();

		nuevo.setUsuario(
				dto.getUsuario());

		nuevo.setCorreo(
				dto.getCorreo());

		nuevo.setNombrePersona(
				dto.getNombrePersona());

		nuevo.setContrasenaHash(
				passwordEncoder.encode(
						dto.getContrasena()));

		nuevo.setFechaNacimiento(
				dto.getFechaNacimiento());

		nuevo.setFechaCreacionCuenta(
				LocalDateTime.now());

		nuevo.setEnLinea(false);

		nuevo.setSobreMi(
				"Hola! Estoy usando WZ");

		usuarioRepo.save(nuevo);

		return nuevo;
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

		Usuario usuario =
				tokenVerificacion.getUsuario();

		usuario.setHabilitado(true);

		usuarioRepo.save(usuario);

		return true;
	}
}
