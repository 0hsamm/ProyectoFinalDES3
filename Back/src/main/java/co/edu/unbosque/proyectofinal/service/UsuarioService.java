package co.edu.unbosque.proyectofinal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.proyectofinal.dto.CrearUsuarioDTO;
import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ModelMapper modelMapper;

	public int create(CrearUsuarioDTO dto) {

		try {

			Usuario usuario = new Usuario();

			usuario.setUsuario(dto.getUsuario());

			usuario.setCorreo(dto.getCorreo());

			usuario.setNombrePersona(dto.getNombrePersona());

			usuario.setContrasenaHash(
					dto.getContrasena());

			usuario.setSobreMi(dto.getSobreMi());

			usuario.setFechaCreacionCuenta(
					LocalDateTime.now());

			usuario.setEnLinea(false);

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

			usuario.setUsuario(dto.getUsuario());

			usuario.setNombrePersona(
					dto.getNombrePersona());

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