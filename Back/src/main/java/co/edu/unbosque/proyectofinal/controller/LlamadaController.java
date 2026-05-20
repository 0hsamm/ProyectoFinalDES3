package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.LlamadaDTO;
import co.edu.unbosque.proyectofinal.dto.LlamadaRespuestaDTO;
import co.edu.unbosque.proyectofinal.service.LlamadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/llamadas")
@Tag(name = "Llamadas", description = "Endpoints para gestionar llamadas de voz y video usando Agora")
public class LlamadaController {

	@Autowired
	private LlamadaService llamadaService;

	@PostMapping("/iniciar")
	@Operation(summary = "Iniciar una llamada",
		description = "Crea la llamada en BD y devuelve el token de Agora para el llamante. "
				+ "El frontend usa este token para conectarse al canal de Agora.")
	public ResponseEntity<?> iniciarLlamada(
			@RequestBody LlamadaDTO dto) {

		LlamadaRespuestaDTO respuesta =
				llamadaService.iniciarLlamada(dto);

		if (respuesta == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Usuario o conversación no encontrados");
		}

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(respuesta);
	}

	@PutMapping("/{id}/aceptar/{usuarioReceptorId}")
	@Operation(summary = "Aceptar una llamada",
		description = "El receptor acepta la llamada. "
				+ "Devuelve el token de Agora para que el receptor se una al canal.")
	public ResponseEntity<?> aceptarLlamada(
			@PathVariable Long id,
			@PathVariable Long usuarioReceptorId) {

		LlamadaRespuestaDTO respuesta =
				llamadaService.aceptarLlamada(id, usuarioReceptorId);

		if (respuesta == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("Llamada no encontrada o usuario incorrecto");
		}

		return ResponseEntity.ok(respuesta);
	}

	@PutMapping("/{id}/finalizar")
	@Operation(summary = "Finalizar una llamada",
		description = "Marca la llamada como FINALIZADA y calcula la duración.")
	public ResponseEntity<String> finalizarLlamada(
			@PathVariable Long id) {

		int resultado = llamadaService.finalizarLlamada(id);

		if (resultado == 1) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("Llamada no encontrada");
		}

		return ResponseEntity.ok("Llamada finalizada correctamente");
	}

	@PutMapping("/{id}/rechazar")
	@Operation(summary = "Rechazar una llamada",
		description = "El receptor rechaza la llamada. Queda registrada como PERDIDA.")
	public ResponseEntity<String> rechazarLlamada(
			@PathVariable Long id) {

		int resultado = llamadaService.rechazarLlamada(id);

		if (resultado == 1) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("Llamada no encontrada");
		}

		return ResponseEntity.ok("Llamada marcada como perdida");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener una llamada por ID")
	public ResponseEntity<?> getById(@PathVariable Long id) {

		LlamadaDTO dto = llamadaService.getById(id);

		if (dto == null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("Llamada no encontrada");
		}

		return ResponseEntity.ok(dto);
	}

	@GetMapping("/conversacion/{conversacionId}")
	@Operation(summary = "Historial de llamadas de una conversación")
	public ResponseEntity<List<LlamadaDTO>> getByConversacion(
			@PathVariable Long conversacionId) {

		List<LlamadaDTO> lista =
				llamadaService.getByConversacion(conversacionId);

		return ResponseEntity.ok(lista);
	}


	@GetMapping("/historial/{usuarioId}")
	@Operation(summary = "Historial de llamadas de un usuario",
		description = "Devuelve todas las llamadas del usuario, tanto las que hizo como las que recibió.")
	public ResponseEntity<List<LlamadaDTO>> getHistorialUsuario(
			@PathVariable Long usuarioId) {

		List<LlamadaDTO> lista =
				llamadaService.getHistorialUsuario(usuarioId);

		return ResponseEntity.ok(lista);
	}

}
