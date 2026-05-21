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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.entity.Usuario;
import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;
import co.edu.unbosque.proyectofinal.service.AuditoriaService;
import co.edu.unbosque.proyectofinal.service.MensajeService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/mensajes")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class MensajeController {

	@Autowired
	private MensajeService mensajeService;
	
	@Autowired
	private AuditoriaService auditoriaService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping
	public ResponseEntity<String> enviarMensaje(
	        @RequestBody MensajeDTO dto,
	        HttpServletRequest request) {

	    String ip = request.getRemoteAddr();
	    String navegador = request.getHeader("User-Agent");

	    int status = mensajeService.create(dto);

	    if (status == 0) {
	        Usuario usuario = dto.getRemitenteId() != null
	                ? usuarioRepository.findById(dto.getRemitenteId()).orElse(null)
	                : null;
	        auditoriaService.registrar(
	                usuario,
	                "ENVIAR_MENSAJE",
	                "MENSAJES",
	                "Mensaje enviado en conversacion: " + dto.getConversacionId(),
	                ip, navegador, null, null, null, true);
	        return new ResponseEntity<>("Mensaje enviado correctamente", HttpStatus.OK);
	    } else if (status == 1) {
	        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.BAD_REQUEST);
	    } else if (status == 4) {
	        return new ResponseEntity<>("El usuario no tiene permiso", HttpStatus.FORBIDDEN);
	    } else if (status == 5) {
	        return new ResponseEntity<>("Datos del mensaje incompletos", HttpStatus.BAD_REQUEST);
	    } else {
	        return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@GetMapping
	public List<MensajeDTO> obtenerTodos(
			@RequestParam(required = false)
			String fraseSecreta) {

		return mensajeService.getAll(fraseSecreta);
	}

	@GetMapping("/conversacion/{id}")
	public List<MensajeDTO> obtenerPorConversacion(
			@PathVariable Long id,
			@RequestParam(required = false)
			String fraseSecreta) {

		return mensajeService
				.getMensajesPorConversacion(id, fraseSecreta);
	}

	@GetMapping("/conversacion/{id}/recientes")
	public List<MensajeDTO> obtenerRecientes(
			@PathVariable Long id,
			@RequestParam(required = false)
			String fraseSecreta) {

		return mensajeService
				.getUltimosMensajes(id, fraseSecreta);
	}

	@DeleteMapping("/{id}")
	public int eliminar(
			@PathVariable Long id) {

		return mensajeService.deleteById(id);
	}
}
