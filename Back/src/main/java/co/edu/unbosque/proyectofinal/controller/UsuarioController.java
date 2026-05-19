package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.UsuarioDTO;
import co.edu.unbosque.proyectofinal.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<String> crear(
			@RequestBody UsuarioDTO dto) {

		int status =
				usuarioService.create(dto);

		if (status == 0) {
			return new ResponseEntity<>(
					"Usuario creado correctamente",
					HttpStatus.CREATED);
		}

		return new ResponseEntity<>(
				"Error al crear el usuario",
				HttpStatus.BAD_REQUEST);
	}

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {

		return new ResponseEntity<>(
				usuarioService.getAll(),
				HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> obtenerPorId(
			@PathVariable Long id) {

		UsuarioDTO usuario =
				usuarioService.getById(id);

		if (usuario != null) {
			return new ResponseEntity<>(
					usuario,
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> eliminar(
			@PathVariable Long id) {

		int status =
				usuarioService.deleteById(id);

		if (status == 0) {
			return new ResponseEntity<>(
					"Usuario eliminado",
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				HttpStatus.NOT_FOUND);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> actualizar(
			@PathVariable Long id,
			@RequestBody UsuarioDTO dto) {

		int status =
				usuarioService.updateById(id, dto);

		if (status == 0) {
			return new ResponseEntity<>(
					"Usuario actualizado",
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				HttpStatus.NOT_FOUND);
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<UsuarioDTO> obtenerPorUsername(
			@PathVariable String username) {

		UsuarioDTO usuario =
				usuarioService.getByUsername(username);

		if (usuario != null) {
			return new ResponseEntity<>(
					usuario,
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				HttpStatus.NOT_FOUND);
	}
}
