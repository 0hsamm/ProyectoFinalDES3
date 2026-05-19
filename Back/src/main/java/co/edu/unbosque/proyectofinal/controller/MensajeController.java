package co.edu.unbosque.proyectofinal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.proyectofinal.dto.CrearMensajeDTO;
import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.enums.TipoMensaje;
import co.edu.unbosque.proyectofinal.service.MensajeService;

@RestController
@RequestMapping("/mensajes")
@CrossOrigin(origins = {"http://localhost:8081", "*"})

public class MensajeController {

	@Autowired
	private MensajeService mensajeService;

	// ENVIAR MENSAJE
	@PostMapping
	public ResponseEntity<String> enviarMensaje(

			@RequestParam String usuario,

			@RequestParam Long conversacionId,

			@RequestParam TipoMensaje tipoMensaje,

			@RequestParam String contenido
	) {

		CrearMensajeDTO dto = new CrearMensajeDTO();

		dto.setUsuario(usuario);

		dto.setConversacionId(conversacionId);

		dto.setTipoMensaje(tipoMensaje);

		dto.setContenido(contenido);

		int status = mensajeService.create(dto);

		if (status == 0) {

			return new ResponseEntity<>(

					"Mensaje enviado correctamente",

					HttpStatus.OK);
		}

		else if (status == 1) {

			return new ResponseEntity<>(

					"Usuario no encontrado",

					HttpStatus.BAD_REQUEST);
		}

		else if (status == 2) {

			return new ResponseEntity<>(

					"Conversación no encontrada",

					HttpStatus.BAD_REQUEST);
		}

		else {

			return new ResponseEntity<>(

					"Error interno al enviar mensaje",

					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// OBTENER TODOS
	@GetMapping
	public List<MensajeDTO> obtenerTodos() {

		return mensajeService.getAll();
	}

	// MENSAJES POR CONVERSACION
	@GetMapping("/conversacion/{id}")
	public List<MensajeDTO> obtenerPorConversacion(
			@PathVariable Long id) {

		return mensajeService
				.getMensajesPorConversacion(id);
	}

	// ULTIMOS MENSAJES
	@GetMapping("/conversacion/{id}/recientes")
	public List<MensajeDTO> obtenerRecientes(
			@PathVariable Long id) {

		return mensajeService
				.getUltimosMensajes(id);
	}

	// ELIMINAR
	@DeleteMapping("/{id}")
	public int eliminar(
			@PathVariable Long id) {

		return mensajeService.deleteById(id);
	}
	
	
	
}