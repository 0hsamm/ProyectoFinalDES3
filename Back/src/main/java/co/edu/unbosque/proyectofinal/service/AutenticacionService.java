package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.LoginDTO;
import co.edu.unbosque.proyectofinal.dto.RegistroDTO;
import co.edu.unbosque.proyectofinal.entity.TokenVerificacion;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.proyectofinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.proyectofinal.repository.TokenVerificacionRepository;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.exception.CorreoYaExisteException;
@Service
public class AutenticacionService {

	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired TokenVerificacionRepository tokenRepo;

	public Usuario registrar(RegistroDTO dto) {

		Optional<Usuario> usuarioExistente =
				usuarioRepo.findByUsuario(
						dto.getUsuario());

		if(usuarioExistente.isPresent()) {

			throw new UsuarioYaExisteException();
		}

		Optional<Usuario> correoExistente =
				usuarioRepo.findByCorreo(
						dto.getCorreo());

		if(correoExistente.isPresent()) {

			throw new CorreoYaExisteException();
		
		}

		if(!UsuarioValidator.validarUsuario(
				dto.getUsuario())) {

			throw new RuntimeException(
					"Usuario inválido");
		}

		if(!UsuarioValidator.validarCorreo(
				dto.getCorreo())) {

			throw new RuntimeException(
					"Correo inválido");
		}

		if(!UsuarioValidator.validarContrasena(
				dto.getContrasena())) {

			throw new RuntimeException(
					"La contraseña debe tener mínimo 8 caracteres");
		}

		if(!UsuarioValidator.validarEdad(
				dto.getFechaNacimiento())) {

			throw new RuntimeException(
					"Debes tener mínimo 13 años");
		}

		Usuario nuevo = new Usuario();

		nuevo.setUsuario(dto.getUsuario());

		nuevo.setCorreo(dto.getCorreo());

		nuevo.setNombrePersona(
				dto.getNombrePersona());

		// DESPUÉS meter BCrypt
		nuevo.setContrasenaHash(
				dto.getContrasena());

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

		if(usuario.isEmpty()) {

			throw new CredencialesInvalidasException();
		}

		Usuario user = usuario.get();

		if(!user.getContrasenaHash()
				.equals(dto.getContrasena())) {

			throw new CredencialesInvalidasException();
		}

		user.setEnLinea(true);

		user.setUltimaVezEnLinea(
				LocalDateTime.now());

		usuarioRepo.save(user);

		return user;
	}
	
	public boolean verificarCuenta(
	        String token){

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