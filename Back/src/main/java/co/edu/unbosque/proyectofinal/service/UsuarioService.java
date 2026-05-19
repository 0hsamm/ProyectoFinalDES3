package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public int create(UsuarioDTO dto) {

		try {

			if (dto == null
					|| dto.getUsuario() == null
					|| dto.getCorreo() == null
					|| dto.getNombrePersona() == null
					|| dto.getContrasena() == null
					|| dto.getContrasena().isBlank()) {

				return 1;
			}

			Usuario usuario =
					new Usuario();

			usuario.setUsuario(
					dto.getUsuario());

			usuario.setCorreo(
					dto.getCorreo());

			usuario.setNombrePersona(
					dto.getNombrePersona());

			usuario.setContrasenaHash(
					passwordEncoder.encode(
							dto.getContrasena()));

			usuario.setSobreMi(
					dto.getSobreMi());

			if (usuario.getSobreMi() == null
					|| usuario.getSobreMi().isBlank()) {

				usuario.setSobreMi("Hola! Estoy usando WZ");
			}

			usuario.setFechaNacimiento(
					dto.getFechaNacimiento());

			usuario.setFechaCreacionCuenta(
					LocalDateTime.now());

			usuario.setEnLinea(false);

			usuario.setUltimaVezEnLinea(null);

			usuario.setHabilitado(false);

			usuarioRepository.save(usuario);

			return 0;

		} catch (Exception e) {

			e.printStackTrace();

			return 1;
		}
	}

	public List<UsuarioDTO> getAll() {

		return usuarioRepository.findAll()
				.stream()
				.map(usuario ->
						modelMapper.map(
								usuario,
								UsuarioDTO.class))
				.toList();
	}

	public UsuarioDTO getById(Long id) {

		return usuarioRepository.findById(id)
				.map(usuario ->
						modelMapper.map(
								usuario,
								UsuarioDTO.class))
				.orElse(null);
	}

	public int deleteById(Long id) {

		try {

			usuarioRepository.deleteById(id);

			return 0;

		} catch (Exception e) {

			e.printStackTrace();

			return 1;
		}
	}

	public int updateById(Long id, UsuarioDTO dto) {

		try {

			Usuario usuario =
					usuarioRepository.findById(id)
							.orElse(null);

			if (usuario == null) {
				return 1;
			}

			if (dto.getUsuario() != null) {
				usuario.setUsuario(
						dto.getUsuario());
			}

			if (dto.getNombrePersona() != null) {
				usuario.setNombrePersona(
						dto.getNombrePersona());
			}

			if (dto.getCorreo() != null) {
				usuario.setCorreo(
						dto.getCorreo());
			}

			if (dto.getSobreMi() != null) {
				usuario.setSobreMi(
						dto.getSobreMi());
			}

			if (dto.getFechaNacimiento() != null) {
				usuario.setFechaNacimiento(
						dto.getFechaNacimiento());
			}

			if (dto.getContrasena() != null
					&& !dto.getContrasena().isBlank()) {

				usuario.setContrasenaHash(
						passwordEncoder.encode(
								dto.getContrasena()));
			}

			usuarioRepository.save(usuario);

			return 0;

		} catch (Exception e) {

			e.printStackTrace();

			return 1;
		}
	}

	public UsuarioDTO getByUsername(
			String username) {

		return usuarioRepository
				.findByUsuario(username)
				.map(usuario ->
						modelMapper.map(
								usuario,
								UsuarioDTO.class))
				.orElse(null);
	}
}
