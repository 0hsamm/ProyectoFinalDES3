package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.proyectofinal.dto.AgregarParticipanteDTO;
import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.CrearConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.ParticipanteConversacionDTO;
import co.edu.unbosque.proyectofinal.enums.TipoConversacion;
import co.edu.unbosque.proyectofinal.service.ConversacionService;

@RestController
@RequestMapping("/conversaciones")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class ConversacionController {

	@Autowired
	private ConversacionService conversacionService;

	@PostMapping
	public ResponseEntity<String> crear(
	        @RequestBody CrearConversacionDTO dto) {

	    int status =
	            conversacionService.create(dto);

	    if (status == 0) {

	        return new ResponseEntity<>(
	                "Conversación creada correctamente",
	                HttpStatus.CREATED);

	    } else {

	        return new ResponseEntity<>(
	                "Error al crear la conversación",
	                HttpStatus.BAD_REQUEST);
	    }
	}

	@GetMapping
	public ResponseEntity<List<ConversacionDTO>> getAll() {

		return new ResponseEntity<>(
				conversacionService.getAll(),
				HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ConversacionDTO> getById(
			@PathVariable Long id) {

		ConversacionDTO dto =
				conversacionService.getById(id);

		if (dto != null) {

			return new ResponseEntity<>(
					dto,
					HttpStatus.OK);

		} else {

			return new ResponseEntity<>(
					HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
			@PathVariable Long id) {

		int status =
				conversacionService.deleteById(id);

		if (status == 0) {

			return new ResponseEntity<>(
					"Conversación eliminada",
					HttpStatus.OK);

		} else {

			return new ResponseEntity<>(
					"No se encontró la conversación",
					HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/agregarParticipante")
	public ResponseEntity<String> agregarParticipante(@RequestBody AgregarParticipanteDTO dto) {

	    int r =
	            conversacionService
	            .agregarParticipante(dto);

	    switch (r) {

	    case 0:
	        return ResponseEntity.ok(
	                "Participante agregado");

	    case 1:
	        return ResponseEntity.badRequest()
	                .body("Conversacion no existe");

	    case 2:
	        return ResponseEntity.badRequest()
	                .body("Usuario no existe");

	    case 3:
	        return ResponseEntity.badRequest()
	                .body("Usuario ya pertenece");

	    default:
	        return ResponseEntity.internalServerError()
	                .body("Error interno");
	    }
	}
	
	@GetMapping("/{id}/participantes") 
	public ResponseEntity<List<ParticipanteConversacionDTO>> getParticipantes(@PathVariable Long id) {

	    return ResponseEntity.ok(conversacionService.getParticipantes(id));
	}
	
	@PostMapping("/{id}/join/{usuario}")
	public ResponseEntity<String> joinCanal(
	        @PathVariable Long id,
	        @PathVariable String usuario) {

	    AgregarParticipanteDTO dto =
	            new AgregarParticipanteDTO();

	    dto.setConversacionId(id);

	    dto.setUsuario(usuario);

	    int r =
	            conversacionService
	            .agregarParticipante(dto);

	    if (r == 0) {
	        return ResponseEntity.ok(
	                "Te uniste al canal");
	    }

	    return ResponseEntity.badRequest()
	            .body("No se pudo unir");
	}

}